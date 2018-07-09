package com.example.innovate.mlkit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import android.util.Log
import com.example.innovate.mlkit.Model.Textmodel
import com.example.innovate.mlkit.Service.Service
import com.example.innovate.mlkit.helpers.MyHelper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudText
import java.io.File
import com.google.cloud.translate.Detection
import com.google.cloud.translate.Translation
import com.google.common.collect.ImmutableList
import java.io.PrintStream


class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    val image: FirebaseVisionImage? = null
    var mBitmap: Bitmap? = null
    val REQUEST_IMG = 101
    var sourceText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camera.setOnClickListener {
            //            checkCameraPermision()
            dispatchTakePictureIntent()
        }
        album.setOnClickListener {
            OpenAlbum()
        }
        traslate.setOnClickListener {
            translateword()
            translatewordObj()
        }

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val extras = data!!.extras
                mBitmap = extras!!.get("data") as Bitmap
                imageView.setImageBitmap(mBitmap)
                Log.d("resultCode:", "" + resultCode)

                runCloudTextRecognition()
            } else if (requestCode == REQUEST_IMG) {

                val imageUri = data!!.data
                val path = MyHelper.getPath(this, imageUri)
                val imgFile = File(path)
                if (imgFile.exists()) {
                    mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                    imageView.setImageBitmap(mBitmap)
                }

                runCloudTextRecognition()


            }

        }
    }


    private fun runCloudTextRecognition() {
        MyHelper.showDialog(this)
        val options = FirebaseVisionCloudDetectorOptions.Builder()
                .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                .setMaxResults(15)
                .build()
        val image = FirebaseVisionImage.fromBitmap(mBitmap!!)
        val detector = FirebaseVision.getInstance().getVisionCloudDocumentTextDetector(options)
        detector.detectInImage(image).addOnSuccessListener { texts ->
            MyHelper.dismissDialog()
            processCloudTextRecognitionResult(texts)
        }.addOnFailureListener { e ->
            MyHelper.dismissDialog()
            e.printStackTrace()
        }
    }

    private fun processCloudTextRecognitionResult(text: FirebaseVisionCloudText?) {
        mTextword.setText(null)
        if (text == null) {
            mTextword.setText("not found")
            return
        }
        val sentenceStr = StringBuilder()
        val pages = text.pages
        for (i in pages.indices) {
            val page = pages[i]
            val blocks = page.blocks
            for (j in blocks.indices) {
                val paragraphs = blocks[j].paragraphs
                for (k in paragraphs.indices) {
                    val paragraph = paragraphs[k]
                    val words = paragraph.words
                    for (l in words.indices) {
                        val symbols = words[l].symbols

                        val wordStr = StringBuilder()
                        for (m in symbols.indices) {
                            wordStr.append(symbols[m].text)
                        }
                        //mTextView.append(wordStr);
                        //mTextView.append(": " + words.get(l).getConfidence());
                        //mTextView.append("\n");

                        sentenceStr.append(wordStr).append(" ")
                    }
                }
            }
        }
        mTextword.append("\n" + sentenceStr)
        sourceText = sentenceStr.toString()
    }

    fun OpenAlbum() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMG)
    }

    fun detectLanguage(sourceText: String, out: PrintStream) {
        var translate: Translate = TranslateOptions.getDefaultInstance().service
        val detections = translate.detect(ImmutableList.of(sourceText))
        println("Language(s) detected:")
        for (detection in detections) {
            out.printf("\t%s\n", detection)
        }
    }

    fun translateword() {
        val key = "AIzaSyBevboMhLWxe3rUnFfTvN_uoaORnj3m2o8"

        val url: String = "https://translation.googleapis.com/language/translate/v2?key=" + key + "&q=" + sourceText + "&target=th"
        Log.v("urllink", url)
        url.httpPost().responseString { request, response, result ->
            when (result) {
                is Result.Success -> {
                    Log.e("network ok:", result.get())

                }
                is Result.Failure -> {
                    Log.e("result:", result.error.message)
                }


            }
        }

    }

    fun translatewordObj() {
        val key = "AIzaSyBevboMhLWxe3rUnFfTvN_uoaORnj3m2o8"
        val url: String = "https://translation.googleapis.com/language/translate/v2?key=" + key + "&q=" + sourceText + "&target=th"
        Log.v("ีurl link", url)

//        url.httpGet().responseObject(Textmodel.ListDeserializer()) { request, response, result ->
//            val (messageText, err) = result
//            messageText?.forEach { messageTexts ->
//                Log.v("messafe suces",messageTexts.)
//            }
        url.httpGet().responseObject(Textmodel.Desrializer()) { request, response, result ->
            val (messagetexts, err) = result
            messagetexts?.forEach { messagetext ->
                Log.v("value text", messagetext.texts)
                Log.e("error", err.toString())
            }
        }

    }


}
