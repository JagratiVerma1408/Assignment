package com.example.assignment.activity

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LogIn : AppCompatActivity() {
    lateinit var btnLogIN: Button
    lateinit var etNumber: EditText
    lateinit var etPassword: EditText
    lateinit var txtForgot:  TextView
    lateinit var txtSignUp: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        progressLayout = findViewById(R.id.progressLayout)
        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("end",false).apply()

        if (ConnectionManager().checkConnectivity(this@LogIn)) {
            progressLayout.visibility = View.GONE

            val isLogIn = sharedPreferences.getBoolean("isLogIn", false)
            if (isLogIn) {
                val intent = Intent(this@LogIn, MainActivity::class.java)
                startActivity(intent)
                finish()
            }


            etNumber = findViewById(R.id.etMobileNumber)
            etPassword = findViewById(R.id.etPassword)
            btnLogIN = findViewById(R.id.btnLogIn)
            txtSignUp = findViewById(R.id.txtSignUp)
            txtForgot = findViewById(R.id.txtForgotPassword)

            btnLogIN.setOnClickListener {
                val number = etNumber.text.toString()
                val pass = etPassword.text.toString()

                if (number.length==10){
                    if (pass.length>3){
                        val queue = Volley.newRequestQueue(this@LogIn)

                        val url = "http://13.235.250.119/v2/login/fetch_result"

                        val jsonPrams = JSONObject()
                        jsonPrams.put("mobile_number",number)
                        jsonPrams.put("password",pass)

                        println(jsonPrams)

                        val jsonObjectRequest =object: JsonObjectRequest(Request.Method.POST, url,jsonPrams, Response.Listener{
                            try {
                                progressLayout.visibility = View.GONE
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                println(data)
                                if(success){
                                    val data1 =data.getJSONObject("data")
                                    sharedPreferences.edit().putString("user_id",data1.getString("user_id")).apply()
                                    sharedPreferences.edit().putString("name",data1.getString("name")).apply()
                                    sharedPreferences.edit().putString("email",data1.getString("email")).apply()
                                    sharedPreferences.edit().putString("mobile_number",data1.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putString("address",data1.getString("address")).apply()
                                    sharedPreferences.edit().putBoolean("isLogIn",true).apply()

                                    val intent = Intent(this@LogIn, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()


                                }else{
                                    Toast.makeText(this@LogIn, "Invalid Mobile Number or Password", Toast.LENGTH_SHORT).show()
                                }
                            }catch (e: JSONException){
                                Toast.makeText(this@LogIn, "Error while connecting...!!!", Toast.LENGTH_SHORT).show()
                            }
                        },Response.ErrorListener {
                            println(it)
                            Toast.makeText(this@LogIn, "Volley error occurred!!!", Toast.LENGTH_SHORT).show()
                        }){
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "48e7c5ea4d1964"
                                return headers
                            }

                        }
                        queue.add(jsonObjectRequest)
                    }else{
                        Toast.makeText(this, "Minimum 4 characters for password required", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
                }

            }

            txtForgot.setOnClickListener {
                val intent = Intent(this@LogIn, ForgotPassword::class.java)
                startActivity(intent)
            }
            txtSignUp.setOnClickListener {
                val intent = Intent(this@LogIn, SignUp::class.java)
                startActivity(intent)
            }
        }else{
            progressLayout.visibility = View.GONE
            val dialogBox = AlertDialog.Builder(this@LogIn)
            dialogBox.setTitle("Error")
            dialogBox.setMessage("Internet Connectivity is not Found")
            dialogBox.setPositiveButton("Open Settings"){ _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialogBox.setNegativeButton("Exit"){ _, _ ->
                ActivityCompat.finishAffinity(this@LogIn)
            }
            dialogBox.create()
            dialogBox.show()
        }
    }
}