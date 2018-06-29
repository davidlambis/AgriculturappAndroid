package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.ContactsContract
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.modules.models.sms.SmsUser
import com.interedes.agriculturappv3.modules.models.sms.Sms_Table
import com.interedes.agriculturappv3.services.resources.EmisorType_Message_Resources
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import java.util.*

class ChatSms_Repository:IMainViewChatSms.Repository {


    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }


    override fun getListSms(context: Activity) {

        ///Adapters
        var smsListUser:ArrayList<SmsUser> = ArrayList<SmsUser>()
        var smsListMessage:ArrayList<Sms> = ArrayList<Sms>()

        val message = Uri.parse("content://sms/")
        val cr = context.contentResolver
        var c = cr.query(message, null, null, null, null)
        val indexBody = c.getColumnIndex("body")
        if (indexBody < 0 || !c.moveToFirst()) return
        context.startManagingCursor(c)
        val totalSMS = c.getCount()
        if (c.moveToFirst()) {
            for (i in 0 until totalSMS) {

                var typeMessage:String?=""
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    typeMessage= MessageSmsType.MESSAGE_TYPE_INBOX
                } else {
                    typeMessage= MessageSmsType.MESSAGE_TYPE_SENT
                }

                val addresSpaceEmpty= c.getString(c
                        .getColumnIndexOrThrow("address")).trim()

                val addresSpaceReplace= addresSpaceEmpty.replace(" ","")



                var objSms = Sms(
                        0,
                        c.getString(c!!.getColumnIndexOrThrow("_id")),
                        addresSpaceReplace,
                        c.getString(c.getColumnIndexOrThrow("body")),
                        c.getString(c.getColumnIndex("read")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        typeMessage,
                        EmisorType_Message_Resources.MESSAGE_TYPE_SMS
                )

                smsListMessage.add(objSms)

                val lastSms = getLastSms()
                if (lastSms == null) {
                    objSms.Id = 1
                } else {
                    objSms.Id = lastSms.Id!! + 1
                }

                objSms.save()


                c.moveToNext()
            }




            /*
            //Ordenar
            Collections.sort(smsListMessage, object : Comparator<Sms> {
                override fun compare(lhs: Sms, rhs: Sms): Int {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return if (lhs._id?.toInt()!! > rhs._id?.toInt()!!) -1 else if (lhs._id?.toInt()!! < rhs._id?.toInt()!!) 1 else 0
                }
            })

            //Reordenar Lista
            Collections.reverse(smsListMessage)

            for (objSms in smsListMessage){

                if(objSms._msg?.contains(context.getString(R.string.idenfication_send_sms_app))!!){

                    var verificateAddress = smsListUser?.filter { smsuser: SmsUser -> smsuser._address?.replace(" ","") == objSms._address?.replace(" ","") }
                    if(verificateAddress.size>0){
                        var item: SmsUser = verificateAddress.get(0)
                        smsListUser?.remove(item)
                    }

                    var newMsg=objSms._msg?.replace(context.getString(R.string.idenfication_send_sms_app),"")
                    val phoneNumber =  objSms._address
                    val contactName = getContactDisplayNameByNumber(phoneNumber,context)
                    var objSmsUser = SmsUser(
                            objSms._id,
                            objSms._address,
                            contactName,
                            newMsg,
                            objSms._readState,
                            objSms._time,
                            objSms._folderName
                    )
                    smsListUser.add(objSmsUser)
                }
            }
            //setListSms(smsListUser)   */
        }



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

}