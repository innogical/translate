package com.example.innovate.mlkit.Model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.*

//
//class Textmodel {
//    @SerializedName("translations")
//    @Expose
//    var translation: List<MessageResult>? = null
//
//
//    class MessageResult {
//        @SerializedName("translatedText")
//        @Expose
//        var message: String? = null
//        @SerializedName("detectedSourceLanguage")
//        @Expose
//        var originalLanguage: String? = null
//
//        constructor(message: String?, originalLanguage: String?) {
//            this.message = message
//            this.originalLanguage = originalLanguage
//        }
//
//    }
//
//    class ListDeserializer : ResponseDeserializable<List<Textmodel>> {
//        override fun deserialize(content: String) =Gson().fromJson<List<Textmodel>>(content,object :TypeToken<List<Textmodel>>(){}.type)
//    }
//}
data class Textmodel(var texts: String, var originalLanguage: String) {
    class Desrializer : ResponseDeserializable<Array<Textmodel>> {
        override fun deserialize(content: String): Array<Textmodel>? = Gson().fromJson(content, Array<Textmodel>::class.java)

    }
}