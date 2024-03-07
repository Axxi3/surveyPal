package com.example.surveypal.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.surveypal.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewBottomSheet : BottomSheetDialogFragment() {
    private var optionAddedListener: OptionAddedListener? = null

    fun setOptionAddedListener(listener: OptionAddedListener) {
        optionAddedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.new_option_dialog, container, false)

        // Find views within the inflated layout
        val addOptionButton = view.findViewById<Button>(R.id.add)
        val optionEditText = view.findViewById<EditText>(R.id.editText2)

        addOptionButton.setOnClickListener {
            val optionText = optionEditText.text.toString().trim()
            if (optionText.isNotEmpty()) {
                optionAddedListener?.onOptionAdded(optionText)
                dismiss() // Dismiss the bottom sheet after adding the option
            } else {
                Toast.makeText(requireContext(), "Please add an Option", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
