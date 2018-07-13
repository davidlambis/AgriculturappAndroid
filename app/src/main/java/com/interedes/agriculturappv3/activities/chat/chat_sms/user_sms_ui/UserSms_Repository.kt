package com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui

import android.app.Activity
import android.net.Uri
import android.provider.BaseColumns
import android.provider.ContactsContract
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui.events.RequestEventUserSms
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.modules.models.sms.Sms_Table
import com.interedes.agriculturappv3.services.resources.EmisorType_Message_Resources
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlin.collections.ArrayList

class UserSms_Repository: IMainViewUserSms.Repository {


    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }


    override fun getListSms(context: Activity,smsUser:Sms?) {
        var smsListMessage:List<Sms> = ArrayList<Sms>()
            ///Adapters
            ///var smsListMessage:ArrayList<Sms> = ArrayList<Sms>()
            val message = Uri.parse("content://sms/")
           // val cr =
            val c = context.contentResolver.query(message, null, null, null, "date ASC")
            val indexBody = c.getColumnIndex("body")
            if (indexBody < 0 || !c.moveToFirst()) {
                postEventOk(RequestEventUserSms.LIST_SMS_EVENT, ArrayList<Sms>(),null)
                //c.close()
                return
            }


            context.startManagingCursor(c)
            val totalSMS = c.getCount()


        while (c.moveToNext()) {
            var typeMessage:String?=""
            if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                typeMessage= MessageSmsType.MESSAGE_TYPE_INBOX
            } else {
                typeMessage= MessageSmsType.MESSAGE_TYPE_SENT
            }

            val mensaje:String= c.getString(c.getColumnIndexOrThrow("body"))

            //typeMessage.equals(MessageSmsType.MESSAGE_TYPE_INBOX) &&
            if( mensaje.contains(context.getString(R.string.idenfication_send_sms_app))){

                val addresSpaceEmpty= c.getString(c
                        .getColumnIndexOrThrow("address")).trim()

                val addresSpaceReplace= addresSpaceEmpty.replace(" ","")

                val newMsg=mensaje.replace(context.getString(R.string.idenfication_send_sms_app),"")
                val contactName = getContactDisplayNameByNumber(addresSpaceReplace,context)

                val objSms = Sms(
                        0,
                        c.getString(c!!.getColumnIndexOrThrow("_id")),
                        addresSpaceReplace,
                        newMsg,
                        c.getString(c.getColumnIndex("read")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        typeMessage,
                        EmisorType_Message_Resources.MESSAGE_EMISOR_TYPE_SMS,
                        contactName
                )

                //smsListMessage.add(objSms)


                val smsExist=SQLite.select().from(Sms::class.java).where(Sms_Table.Id_Message.eq(objSms.Id_Message)).querySingle()
                if(smsExist!=null){
                    objSms.Id=smsExist?.Id
                }else{
                    val lastSms = getLastSms()
                    if (lastSms == null) {
                        objSms.Id = 1
                    } else {
                        objSms.Id = lastSms.Id!! + 1
                    }
                }
                objSms.save()
            }
        }

        smsListMessage= SQLite.select().from(Sms::class.java).groupBy(Sms_Table.Address).orderBy(Sms_Table.Id,false).queryList()
        postEventOk(RequestEventUserSms.LIST_SMS_EVENT,smsListMessage,null)
    }

     fun getLastSms(): Sms? {
        val lastCultivo = SQLite.select().from(Sms::class.java).where().orderBy(Sms_Table.Id, false).querySingle()
        return lastCultivo
    }


    fun getContactDisplayNameByNumber(number: String?,context: Activity): String {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        var name = "Desconocido"
        val contentResolver = context.contentResolver
        val contactLookup = contentResolver.query(uri, arrayOf(BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
        try {
            if (contactLookup != null && contactLookup.count > 0) {
                contactLookup.moveToNext()
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME))
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            contactLookup?.close()
        }
        return name
    }



    //region Events

    private fun postEventOk(type: Int, lisSms: List<Sms>?, sms: Sms?) {
        var smsistMutable:MutableList<Object>? = null
        var smsMutable: Object? = null
        if (sms != null) {
            smsMutable = sms as Object
        }
        if (lisSms != null) {
            smsistMutable=lisSms as MutableList<Object>
        }

        postEvent(type, smsistMutable, smsMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventUserSms(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}