package com.interedes.agriculturappv3.asistencia_tecnica.activities.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        fabLogin?.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabLogin -> {
                ingresar()
            }

        }
    }


    private fun ingresar() {
        progressBar.visibility = View.VISIBLE
        startActivity(Intent(this, MenuMainActivity::class.java))
    }
}
