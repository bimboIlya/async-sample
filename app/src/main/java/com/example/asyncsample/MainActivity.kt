package com.example.asyncsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels

import com.example.asyncsample.AsyncOption.*

class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MyViewmodel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeUi()
    }

    private fun observeUi() {
        viewmodel.chosenAsyncOption.observe(this,
            { Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show() })
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

