package com.interedes.agriculturappv3.productor.models.ventas.resports

import android.os.Parcel
import com.interedes.agriculturappv3.productor.models.parcelable.KParcelable
import com.interedes.agriculturappv3.productor.models.parcelable.parcelableCreator
import com.interedes.agriculturappv3.productor.models.parcelable.readBoolean
import com.interedes.agriculturappv3.productor.models.parcelable.writeBoolean


class Artist(var name: String?, var isFavorite: Boolean?): KParcelable {

    private constructor(p: Parcel) : this(
            name = p.readString(),
            isFavorite = p.readBoolean())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeBoolean(isFavorite!!)
    }

    companion object {
        @JvmField val CREATOR = parcelableCreator(::Artist)
    }
}