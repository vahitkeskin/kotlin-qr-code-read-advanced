package com.vahitkeskin.kotlinadvancedqrcode

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.zxing.integration.android.IntentIntegrator
import com.vahitkeskin.kotlinadvancedqrcode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.ibQrCodeOpen.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setPrompt(getString(R.string.app_name))
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null && result.contents != null) {
                scannerResult(result.contents)
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun scannerResult(result: String) {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_message)
        val dialogTitle: TextView = dialog.findViewById(R.id.dialogTitle)
        val dialogMessage: TextView = dialog.findViewById(R.id.dialogMessage)
        val dialogGoogle: Button = dialog.findViewById(R.id.dialogGoogle)
        val dialogCopy: Button = dialog.findViewById(R.id.dialogCopy)
        val dialogClose: Button = dialog.findViewById(R.id.dialogClose)

        dialogTitle.text = "Scanner Result"
        dialogMessage.text = result

        if (result.contains("http") || result.contains(".com")) {
            dialogGoogle.isVisible = true
            dialogCopy.isVisible = false
            dialogMessage.setTextColor(resources.getColor(android.R.color.holo_blue_dark))
            dialogMessage.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        } else {
            dialogCopy.isVisible = true
            dialogGoogle.isVisible = false
        }

        dialogGoogle.setOnClickListener {
            goToBrowser(result)
        }

        dialogMessage.setOnClickListener {
            if (result.contains("http") || result.contains(".com")) {
                goToBrowser(result)
            }
        }

        dialogCopy.setOnClickListener {
            val clipBoard: ClipboardManager =
                this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("text label", result)
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(this, "\"$result\" Copy", Toast.LENGTH_LONG).show()
        }
        dialogClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun goToBrowser(link: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        startActivity(intent)
        Toast.makeText(this, "Browser...", Toast.LENGTH_LONG).show()
    }
}