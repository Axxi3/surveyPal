package com.example.surveypal.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.surveypal.DataModels.User
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentRegisterBinding
import com.example.surveypal.utils.isValidEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val TAG="Register Fragment"
class RegisterFragment : Fragment() {
private lateinit var binding: FragmentRegisterBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        binding.Login.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment2_to_loginFragment22)
        }


        binding.createAccount.setOnClickListener{
            binding.createAccount.startAnimation()
            if(binding.name.text.isEmpty()){
                binding.createAccount.revertAnimation()
                Toast.makeText(requireContext(), "Please Type the name", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.email.text.isEmpty() || !isValidEmail(binding.email.text.toString())){
                    binding.createAccount.revertAnimation()
                    Toast.makeText(requireContext(), "Please type a valid email", Toast.LENGTH_SHORT).show()
                }
                else {
                    Log.d(TAG, "onViewCreated: " + binding.password.text.toString() + "     " + binding.confirmpass.text.toString())
                    if(binding.password.text.toString() == binding.confirmpass.text.toString() && isPasswordValid(binding.confirmpass.text.toString())){
                        SigninUser(binding.email.text.toString(),binding.password.text.toString())
                    }else{
                        binding.createAccount.revertAnimation()
                        Toast.makeText(requireContext(), "Please type a valid and same passowrd", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun SigninUser(email: String, password: String) {
        Log.d(TAG, "SigninUser: Function called")
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeData(User(binding.name.text.toString(),binding.email.text.toString(),firebaseAuth.uid.toString(),"https://ui-avatars.com/api/?name=${binding.name.text.toString().take(1)}&background=0D8ABC&color=fff"))
                    Log.d(TAG, "SigninUser: Account Successfully created")
                    Toast.makeText(requireContext(), "Account Successfully created, please log in", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment2_to_loginFragment22)
                } else {
                    Log.e(TAG, "SigninUser: Error signing in", task.exception)
                    Toast.makeText(requireContext(), "Error signing in: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun storeData(user: User) {
        firestore.collection("users").document(firebaseAuth.uid.toString()).set(user).addOnCompleteListener { 
            if(it.isSuccessful){
                Log.d(TAG, "storeData: " + "Data successfully stored")
            } else {
                Log.d(TAG, "storeData: ${it.exception}")
            }
        }
    }


    fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()
        return password.matches(passwordRegex)
    }





}