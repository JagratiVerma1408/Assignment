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

class SignUp : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etNumber: EditText
    lateinit var etMail: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        title="Register"

        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)


        etName = findViewById(R.id.etName)
        etNumber = findViewById(R.id.etMobile)
        etMail = findViewById(R.id.etMail)
        etAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPass)
        etConfirmPassword = findViewById(R.id.etConfirmPass)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        btnRegister.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this@SignUp)){
                val name:String? = etName.text.toString()
                val mail:String? = etMail.text.toString()
                val number:String? = etNumber.text.toString()
                val address:String? = etAddress.text.toString()
                val pass:String? = etPassword.text.toString()
                val confirmPass:String? = etConfirmPassword.text.toString()

                if(name!!.isEmpty() ||mail!!.isEmpty()||address!!.isEmpty()||pass!!.isEmpty()||confirmPass!!.isEmpty()|| pass.length<=3) {
                    Toast.makeText(this@SignUp, "Invalid Entry", Toast.LENGTH_LONG).show()
                }else if(confirmPass!=pass){
                    Toast.makeText(this@SignUp, "Password Not Matched", Toast.LENGTH_LONG).show()
                }else{
                    val queue = Volley.newRequestQueue(this@SignUp)

                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonPrams = JSONObject()
                    jsonPrams.put("name",name)
                    jsonPrams.put("mobile_number",number)
                    jsonPrams.put("password",pass)
                    jsonPrams.put("address",address)
                    jsonPrams.put("email",mail)

                    val jsonObjectRequest =object: JsonObjectRequest(Method.POST, url,jsonPrams, Response.Listener{
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if(success){
                                val data1 =data.getJSONObject("data")
                                sharedPreferences.edit().clear().apply()
                                sharedPreferences.edit().putString("user_id",data1.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name",data1.getString("name")).apply()
                                sharedPreferences.edit().putString("email",data1.getString("email")).apply()
                                sharedPreferences.edit().putString("mobile_number",data1.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("address",data1.getString("address")).apply()
                                sharedPreferences.edit().putBoolean("isLogIn",true).apply()

                                Toast.makeText(this@SignUp,"Connection Success",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@SignUp, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                Toast.makeText(this@SignUp, data.getString("errorMessgae"), Toast.LENGTH_SHORT).show()
                            }
                        }catch (e: JSONException){
                            System.out.println(e)
                            Toast.makeText(this@SignUp, "Error while connecting....!!!", Toast.LENGTH_SHORT).show()
                            Toast.makeText(this@SignUp, e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    },
                        Response.ErrorListener {
                        Toast.makeText(this@SignUp, "Error while connecting....!!!", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Internet connection not found..!!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}