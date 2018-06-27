package com.interedes.agriculturappv3.modules.models.Notification

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class ResponsePostNotification(@SerializedName("multicast_id")
                                    var multicast_id: BigDecimal? = null,

                                    @SerializedName("success")
                                    var success: Long? = null,

                                    @SerializedName("failure")
                                    var failure: Long? = null,
                                    @SerializedName("canonical_ids")
                                    var canonical_ids: Long? = null

) {

}


/*


{
    "multicast_id": 4633772173736022138,
    "success": 1,
    "failure": 0,
    "canonical_ids": 0,
    "results": [
        {
            "message_id": "0:1530127066402561%1332ba78f9fd7ecd"
        }
    ]
}
 */