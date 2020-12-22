package com.example.assignment.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPassword : AppCompatActivity() {
    lateinit var etOtp: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPass: EditText
    lateinit var btnSubmit: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOtp = findViewById(R.id.etOtp)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPass = findViewById(R.id.etConfirmPass)
        btnSubmit = findViewById(R.id.btnSubmit)
        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)

        btnSubmit.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this@ResetPassword)){

                val otp:String? = etOtp.text.toString()
                val password:String? = etPassword.text.toString()
                val confirmPass:String? = etConfirmPass.text.toString()

                if(password != confirmPass) {
                    Toast.makeText(this@ResetPassword, "Invalid Entry", Toast.LENGTH_LONG).show()
                }else{
                    val queue = Volley.newRequestQueue(this@ResetPassword)

                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                    val jsonPrams = JSONObject()
                    jsonPrams.put("mobile_number",sharedPreferences.getString("mobile_number",""))
                    jsonPrams.put("password",password)
                    jsonPrams.put("otp",otp)

                    val jsonObjectRequest =object: JsonObjectRequest(
                        Method.POST, url,jsonPrams, Response.Listener{
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if(success){
                                    val msg =data.getString("successMessage")

                                    Toast.makeText(this@ResetPassword,msg,Toast.LENGTH_LONG).show()
                                    val intent = Intent(this@ResetPassword, LogIn::class.java)
                                    startActivity(intent)
                                    finish()

                                }else{
                                    val msg =data.getString("errorMessage")
                                    Toast.makeText(this@ResetPassword, msg, Toast.LENGTH_SHORT).show()
                                }
                            }catch (e: JSONException){
                                Toast.makeText(this@ResetPassword, "Error while connecting....!!!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(this@ResetPassword, "Error while connecting....!!!", Toast.LENGTH_SHORT).show()
                        }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "48e7c5ea4d1964"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                }
            }else{
                val dialogBox = AlertDialog.Builder(this@ResetPassword)
                dialogBox.setTitle("Error")
                dialogBox.setMessage("Internet Connectivity is not Found")
                dialogBox.setPositiveButton("Open Settings"){ _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialogBox.setNegativeButton("Exit"){ _, _ ->
                    ActivityCompat.finishAffinity(this@ResetPassword)
                }
                dialogBox.create()
                dialogBox.show()
            }
        }
    }
}