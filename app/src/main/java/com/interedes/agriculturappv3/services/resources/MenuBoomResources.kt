package com.interedes.agriculturappv3.services.resources

import android.graphics.Color
import com.interedes.agriculturappv3.R
import java.util.*

class MenuBoomResources {


    companion object {

        val CHAT:String="Chat"
        val SINCRONIZAR:String="Sincronizar"
        val EXPORTAR:String="Exportar"
        val IMPORTAR:String="Importar"


        var drawablesResourceProductor:IntArray = intArrayOf(R.drawable.ic_stat_chat, R.drawable.ic_action_backup, R.drawable.ic_icon_database_backup, R.drawable.ic_icon_database_restore)
        val circleSubButtonTextsProductor:Array<String> = arrayOf(CHAT,SINCRONIZAR,EXPORTAR,IMPORTAR)

        var drawablesResourceComprador:IntArray = intArrayOf(R.drawable.ic_stat_chat)
        val circleSubButtonTextsComprador:Array<String>  = arrayOf(CHAT)



        private val Colors = arrayOf("#F44336", "#E91E63", "#9C27B0", "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#9E9E9E", "#607D8B")


        fun GetRandomColor(): Int {
            val random = Random()
            val p = random.nextInt(Colors.size)
            return Color.parseColor(Colors[p])
        }
    }



}