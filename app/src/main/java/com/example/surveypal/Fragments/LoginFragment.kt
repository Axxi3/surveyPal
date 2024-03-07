package com.example.surveypal.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.surveypal.MainActivity
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentLoginBinding
import com.example.surveypal.databinding.FragmentRegisterBinding
import com.example.surveypal.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth

private  val TAG="Login Fragment"
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLoginBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth=FirebaseAuth.getInstance()
        binding.registerbutton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_registerFragment22)
        }

        binding.createAccount.setOnClickListener {
            binding.createAccount.startAnimation()
            if(binding.email.text.toString().isNotEmpty() && isValidEmail(binding.email.text.toString())){
                if(binding.pass.text.isNotEmpty()){
                    checkCredentials(binding.email.text.toString(),binding.pass.text.toString())
                } else {
                    Toast.makeText(requireContext(), "Password can't be empty", Toast.LENGTH_SHORT)
                        .show()
                    binding.createAccount.revertAnimation()
                }
            } else{
                Toast.makeText(requireContext(), "Type a valid email address", Toast.LENGTH_SHORT)
                    .show()
                binding.createAccount.revertAnimation()
            }
        }
    }

    private fun checkCredentials(email: String, pass: String) {
        firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
            if(it.isSuccessful){
                Log.d(TAG, "checkCredentials: SuccessFull Login" )
                binding.createAccount.revertAnimation()
                startActivity(Intent(requireActivity(),MainActivity::class.java))
            } else {
                Log.d(TAG, "checkCredentials: "+it.exception)
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                binding.createAccount.revertAnimation()
            }
        }
    }





}