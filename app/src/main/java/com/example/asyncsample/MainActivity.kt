package com.example.asyncsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels

import com.example.asyncsample.AsyncOption.*
import com.example.asyncsample.databinding.ActivityMainBinding
import com.example.asyncsample.di.Injectable
import com.example.asyncsample.di.ViewModelFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable {

    lateinit var binding: ActivityMainBinding

    @Inject lateinit var vmFactory: ViewModelFactory
    private val viewmodel by viewModels<MyViewmodel>{ vmFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeUi()
    }

    private fun observeUi() {
        viewmodel.chosenAsyncOption.observe(this,
            { binding.textView.text = it.toString() })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        when (viewmodel.chosenAsyncOption.value) {
            RXJAVA -> menu?.findItem(R.id.action_rx_java)?.isChecked = true
            COROUTINES -> menu?.findItem(R.id.action_coroutines)?.isChecked = true
            FLOW -> menu?.findItem(R.id.action_flow)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_rx_java -> {
                item.isChecked = !item.isChecked
                viewmodel.setAsyncOption(RXJAVA)
                true
            }
            R.id.action_coroutines -> {
                item.isChecked = !item.isChecked
                viewmodel.setAsyncOption(COROUTINES)
                true
            }
            R.id.action_flow -> {
                item.isChecked = !item.isChecked
                viewmodel.setAsyncOption(FLOW)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
}

