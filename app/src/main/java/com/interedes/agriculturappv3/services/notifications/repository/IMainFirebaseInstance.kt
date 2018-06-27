package com.interedes.agriculturappv3.services.notifications.repository

interface IMainFirebaseInstance {

    interface Repository {
         fun syncToken(string:String?)
    }
}