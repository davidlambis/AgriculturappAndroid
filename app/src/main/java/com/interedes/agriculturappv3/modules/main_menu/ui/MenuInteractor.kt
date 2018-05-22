package com.interedes.agriculturappv3.modules.main_menu.ui

class MenuInteractor:MainViewMenu.Interactor {


    var repository: MainViewMenu.Repository? = null

    init {
        this.repository = MenuRepository()
    }


    override fun syncData() {
       repository?.syncData()
    }

    override fun syncQuantityData() {
        repository?.syncQuantityData()
    }

}