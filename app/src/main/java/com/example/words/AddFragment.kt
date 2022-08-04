package com.example.words

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.words.databinding.FragmentAddBinding

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<WordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSubmit.isEnabled = false
        binding.editTextEnglish.requestFocus()
        WindowCompat.getInsetsController(requireActivity().window, view).show(WindowInsetsCompat.Type.ime())

        val textWatcher: (Editable?) -> Unit = {
            binding.buttonSubmit.isEnabled = binding.editTextEnglish.text.toString().trim().isNotEmpty()
                    && binding.editTextChinese.text.toString().trim().isNotEmpty()
        }
        binding.apply {
            editTextEnglish.addTextChangedListener {
                binding.buttonSubmit.isEnabled = binding.editTextEnglish.text.toString().trim().isNotEmpty()
                        && binding.editTextChinese.text.toString().trim().isNotEmpty()
            }
            editTextEnglish.addTextChangedListener(afterTextChanged = textWatcher)
            editTextChinese.addTextChangedListener(afterTextChanged = textWatcher)
            buttonSubmit.setOnClickListener {
                viewModel.insertWords(
                    Word(
                        word = editTextEnglish.text.toString().trim(),
                        chineseMeaning = editTextChinese.text.toString().trim()
                    )
                )
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}