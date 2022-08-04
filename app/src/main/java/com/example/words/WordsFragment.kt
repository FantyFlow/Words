package com.example.words

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.example.words.databinding.FragmentWordsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val VIEW_TYPE_SHP = "VIEW_TYPE_SHP"
const val IS_USING_CARD_VIEW = "IS_USING_CARD_VIEW"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(VIEW_TYPE_SHP)

class WordsFragment : Fragment() {
    private var _binding: FragmentWordsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<WordViewModel>()
    private val dataStore by lazy { requireContext().dataStore }
    private val viewType by lazy {
        dataStore.data.map {
            it[booleanPreferencesKey(IS_USING_CARD_VIEW)] ?: false
        }.flowOn(Dispatchers.IO)
    }
    private val myAdapter1 by lazy { MyAdapter(false, viewModel) }
    private val myAdapter2 by lazy { MyAdapter(true, viewModel) }
    private val dividerItemDecoration by lazy {
        DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
    private val allWord by lazy { mutableListOf<Word>() }
    private lateinit var filteredWords: LiveData<List<Word>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordsBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                    val searchView: SearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
                    searchView.maxWidth = Int.MAX_VALUE
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String): Boolean {
                            newText.trim().let { pattern ->
                                filteredWords.removeObservers(viewLifecycleOwner)
                                filteredWords = viewModel.findWordsWithPattern(pattern)
                                filteredWords.observe(viewLifecycleOwner) {
                                    allWord.clear()
                                    allWord.addAll(it)
                                    if (myAdapter1.itemCount != it.size) {
                                        myAdapter1.submitList(it)
                                        myAdapter2.submitList(it)
                                    }
                                }
                            }
                            return true
                        }
                    })
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.app_bar_clear -> MaterialAlertDialogBuilder(requireContext())
                            .setTitle("清空数据")
                            .setPositiveButton("确定") { _, _ ->
                                viewModel.deleteAllWords()
                            }
                            .setNegativeButton("取消") { _, _ ->
                            }
                            .show()

                        R.id.app_bar_switch -> viewLifecycleOwner.lifecycleScope.launch {
                            binding.recyclerView.apply {
                                if (viewType.first()) {
                                    adapter = myAdapter1
                                    addItemDecoration(dividerItemDecoration)

                                } else {
                                    adapter = myAdapter2
                                    removeItemDecoration(dividerItemDecoration)
                                }
                            }
                            dataStore.edit {
                                it[booleanPreferencesKey(IS_USING_CARD_VIEW)] = !viewType.first()
                            }
                        }
                    }
                    return true
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewType.first()) {
                    adapter = myAdapter2
                } else {
                    adapter = myAdapter1
                    addItemDecoration(dividerItemDecoration)
                }
            }
            itemAnimator = object : DefaultItemAnimator() {
                override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
                    super.onAnimationFinished(viewHolder)
                    val linearLayoutManager = layoutManager as LinearLayoutManager
                    val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
                    val lastPosition = linearLayoutManager.findLastVisibleItemPosition()
                    for (i in firstPosition..lastPosition) {
                        (findViewHolderForAdapterPosition(i) as? MyAdapter.MyViewHolder)?.mTextViewNumber?.text =
                            getString(R.string.text_number, i + 1)
                    }
                }
            }
        }
        filteredWords = viewModel.allWordsLive
        filteredWords.observe(viewLifecycleOwner) {
            allWord.clear()
            allWord.addAll(it)
            val tmp = myAdapter1.itemCount
            if (tmp != it.size) {
                if (tmp < it.size) {
                    binding.recyclerView.smoothScrollBy(0, -200)
                }
                myAdapter1.submitList(it)
                myAdapter2.submitList(it)
            }
        }
        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.START or ItemTouchHelper.END
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val wordFrom = allWord[viewHolder.adapterPosition]
                    val wordTo = allWord[target.adapterPosition]
                    wordFrom.id = wordTo.id.also {
                        wordTo.id = wordFrom.id
                    }
                    viewModel.updateWords(wordFrom, wordTo)
                    myAdapter1.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    myAdapter2.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val wordToDelete = allWord[viewHolder.adapterPosition]
                    viewModel.deleteWords(wordToDelete)
                    Snackbar.make(view, "删除了一个词汇", Snackbar.LENGTH_SHORT)
                        .setAction("撤销") {
                            viewModel.insertWords(wordToDelete)
                        }
                        .show()
                }
            }
        ).attachToRecyclerView(binding.recyclerView)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_WordsFragment_to_AddFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}