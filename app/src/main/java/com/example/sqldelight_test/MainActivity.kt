package com.example.sqldelight_test

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Database
import com.example.shared.A
import com.example.shared.B
import com.example.shared.DatabaseHelper
import com.example.sqldelight_test.ui.theme.SqldelighttestTheme
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SqldelighttestTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val viewModel: TestViewModel = viewModel()
                    val data by viewModel.data.collectAsState(emptyList())
                    MainScreen(data)
                }
            }
        }
    }
}

@Composable
fun MainScreen(data: List<A>) {
    LazyColumn {
       items(data) {
           Row {
               Text("${it.name}      ${it.number}         ${it.b.data}")
           }
           Divider()
       }
    }
}

class TestViewModel(context: Application) : AndroidViewModel(context) {
    val dbhelper = DatabaseHelper(AndroidSqliteDriver(Database.Schema, getApplication(), "test.db"))
    val data = dbhelper.getAll()

    init {
        viewModelScope.launch {
            if (dbhelper.getAll().first().isEmpty()) {
                listOf(
                    A("A", 0, B(0, "A0")),
                    A("B", 1, B(1, "B1")),
                    A("C", 2, B(2, "C2")),
                ).forEach {
                    dbhelper.insert(it)
                }
            }
        }
    }

    fun insert(entry: A) {
        viewModelScope.launch {
            dbhelper.insert(entry)
        }
    }
}
