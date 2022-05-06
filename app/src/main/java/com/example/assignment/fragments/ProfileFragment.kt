package com.example.assignment.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.assignment.R


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var txtName: TextView
    lateinit var txtNumber: TextView
    lateinit var txtMail: TextView
    lateinit var txtAddress: TextView
    lateinit var btnSignOut: Button

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = activity!!.getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
        txtName = view.findViewById(R.id.txtDisplayName)
        txtNumber = view.findViewById(R.id.txtDisplayNumber)
        txtMail = view.findViewById(R.id.txtDisplayMail)
        txtAddress = view.findViewById(R.id.txtDisplayAddress)

        val name:String? = "Name : "+sharedPreferences.getString("name", "")
        val number: String? = "Number : "+sharedPreferences.getString("mobile_number", "")
        val mail: String? = "Email Id : "+sharedPreferences.getString("email", "")
        val address:String? = "Address : "+sharedPreferences.getString("address", "")

        txtName.text = name
        txtNumber.text = number
        txtMail.text = mail
        txtAddress.text = address

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}