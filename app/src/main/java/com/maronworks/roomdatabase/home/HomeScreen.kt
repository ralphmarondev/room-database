package com.maronworks.roomdatabase.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maronworks.roomdatabase.home.components.AddTodoDialog
import com.maronworks.roomdatabase.home.components.TodoCard
import com.maronworks.roomdatabase.home.model.Todo
import com.maronworks.roomdatabase.ui.theme.RoomDatabaseTheme

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var title by rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Room Database",
                        fontFamily = FontFamily.Monospace
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "New") },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = ""
                    )
                },
                onClick = { showDialog = !showDialog }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(3) {
                TodoCard(
                    modifier = Modifier
                        .padding(10.dp),
                    todo = Todo(
                        title = "Hello World",
                        date = "2024-05-20 | 7:28PM"
                    ),
                    onClick = {}
                )
            }
        }

        if (showDialog) {
            AddTodoDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = title,
                onValueChange = { title = it },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    RoomDatabaseTheme {
        HomeScreen()
    }
}