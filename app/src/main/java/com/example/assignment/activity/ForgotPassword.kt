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

class ForgotPassword : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etMail: EditText
    lateinit var btnNext: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        etMobileNumber = findViewById(R.id.etMobileNumber)
        etMail = findViewById(R.id.etMail)
        btnNext = findViewById(R.id.btnNext)
        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)

        btnNext.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this@ForgotPassword)){

                val mail:String? = etMail.text.toString()
                val number:String? = etMobileNumber.text.toString()

                if(mail==null||number==null) {
                    Toast.makeText(this@ForgotPassword, "Invalid Entry", Toast.LENGTH_LONG).show()
                }else{
                    val queue = Volley.newRequestQueue(this@ForgotPassword)

                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                    val jsonPrams = JSONObject()
                    jsonPrams.put("email",mail)
                    jsonPrams.put("mobile_number",number)

                    val jsonObjectRequest =object: JsonObjectRequest(
                        Method.POST, url,jsonPrams, Response.Listener{
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if(success){
                                val firstTry =data.getBoolean("first_try")

                                sharedPreferences.edit().clear().apply()
                                sharedPreferences.edit().putString("mobile_number",number).apply()
                                if(!firstTry){
                                    Toast.makeText(this@ForgotPassword,"OTP already sent to your email",Toast.LENGTH_LONG).show()
                                }
                                val intent = Intent(this@ForgotPassword, ResetPassword::class.java)
                                startActivity(intent)
                                finish()

                            }else{

                                Toast.makeText(this@ForgotPassword, "Invalid Number or Email", Toast.LENGTH_SHORT).show()
                            }
                        }catch (e: JSONException){
                            Toast.makeText(this@ForgotPassword, "Error while connecting to server!!!", Toast.LENGTH_SHORT).show()
                        }
                    },
                        Response.ErrorListener {
                            Toast.makeText(this@ForgotPassword, "Error while connecting to server!!!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this,"Internet Connection not found",Toast.LENGTH_SHORT).show()
            }

        }

    }
}