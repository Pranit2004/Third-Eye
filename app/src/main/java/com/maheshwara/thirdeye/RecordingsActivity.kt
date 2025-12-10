package com.maheshwara.thirdeye

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class RecordingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recordings)

        val listView: ListView = findViewById(R.id.listViewFiles)

        // 1. Get the list of files
        val dir = getExternalFilesDir(null)
        // Filter for only Video (.mp4) and Audio (.3gp) files
        val files = dir?.listFiles { file ->
            file.name.endsWith(".3gp") || file.name.endsWith(".mp4")
        } ?: emptyArray()

        // 2. Sort them (Newest first)
        files.sortByDescending { it.lastModified() }

        // 3. Display names in the list
        val fileNames = files.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)
        listView.adapter = adapter

        // 4. Handle CLICK (Open/Play)
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedFile = files[position]
            openFile(selectedFile)
        }

        // 5. Handle LONG CLICK (Share)
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val selectedFile = files[position]
            shareFile(selectedFile)
            true // Return true to indicate we handled the click
        }
    }

    private fun openFile(file: File) {
        try {
            // Securely get the URI
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

            val intent = Intent(Intent.ACTION_VIEW)
            // Determine if it is audio or video
            val mimeType = if (file.name.endsWith(".mp4")) "video/*" else "audio/*"

            intent.setDataAndType(uri, mimeType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No app found to play this file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

            val intent = Intent(Intent.ACTION_SEND)
            val mimeType = if (file.name.endsWith(".mp4")) "video/*" else "audio/*"
            intent.type = mimeType
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(Intent.createChooser(intent, "Share Evidence via..."))
        } catch (e: Exception) {
            Toast.makeText(this, "Could not share file", Toast.LENGTH_SHORT).show()
        }
    }
}