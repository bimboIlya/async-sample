package com.example.asyncsample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.asyncsample.AsyncOption.*
import com.example.asyncsample.DataOption.*
import com.example.asyncsample.databinding.ActivityMainBinding
import com.example.asyncsample.di.Injectable
import com.example.asyncsample.di.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable {

    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var vmFactory: ViewModelFactory
    private val myViewmodel by viewModels<MyViewmodel> { vmFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            viewmodel = myViewmodel
            lifecycleOwner = this@MainActivity
        }

        observeUi()
    }

    private fun observeUi() {
        myViewmodel.message.observe(this, {
            it?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                myViewmodel.messageShown()
            }
        })

        myViewmodel.chosenDataOption.observe(this, {
            it?.let {
                when (it) {
                    USER -> binding.usersButton.isChecked = true
                    POST -> binding.postsButton.isChecked = true
                    COMMENT -> binding.commentsButton.isChecked = true
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        when (myViewmodel.chosenAsyncOption.value) {
            RXJAVA -> menu?.findItem(R.id.action_rx_java)?.isChecked = true
            COROUTINES -> menu?.findItem(R.id.action_coroutines)?.isChecked = true
            CHANNELS -> menu?.findItem(R.id.action_channels)?.isChecked = true
            FLOW -> menu?.findItem(R.id.action_flow)?.isChecked = true
            DATABASE -> menu?.findItem(R.id.action_db)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_rx_java -> {
                item.isChecked = !item.isChecked
                myViewmodel.setAsyncOption(RXJAVA)
                true
            }
            R.id.action_coroutines -> {
                item.isChecked = !item.isChecked
                myViewmodel.setAsyncOption(COROUTINES)
                true
            }
            R.id.action_channels -> {
                item.isChecked = !item.isChecked
                myViewmodel.setAsyncOption(CHANNELS)
                true
            }
            R.id.action_flow -> {
                item.isChecked = !item.isChecked
                myViewmodel.setAsyncOption(FLOW)
                true
            }
            R.id.action_db -> {
                item.isChecked = !item.isChecked
                myViewmodel.setAsyncOption(DATABASE)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
}

