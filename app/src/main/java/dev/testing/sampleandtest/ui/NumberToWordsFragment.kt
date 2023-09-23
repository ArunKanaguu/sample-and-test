package dev.testing.sampleandtest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.testing.sampleandtest.common.Currency
import dev.testing.sampleandtest.databinding.FragmentNumberToWordsBinding

class NumberToWordsFragment : Fragment() {
    private var _binding: FragmentNumberToWordsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNumberToWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            currency.setOnClickListener {
             val d = Currency().currency(numbers.text.toString(),false)
                words.text = d.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val TAG = "NumberToWordsFragment"
    }
}