# Setting up Room Database

- Open internet this will need to download dependencies

# Gradle

gradle/libs.versions.toml

> before [libraries]

```
roomCommon = "2.6.1"
roomCompiler = "2.6.1"
roomRuntime = "2.6.1"
compose = "1.6.7"
```

> on [libraries]

```
androidx-room-compiler = { module = "androidx.room:room-compiler", version.ref = "roomCompiler" }
androidx-room-runtime = { module = "androidx.room:room-runtime", version.ref = "roomRuntime" }

androidx-room-common = { group = "androidx.room", name = "room-common", version.ref = "roomCommon" }
compose-runtime-livedata = { group = "androidx.compose.runtime", name = "runtime-livedata", version.ref = "compose" }
```

apps/build.gradle.kts

```
plugins {
    ...
    id("kotlin-kapt")
}

dependencies{
    ...
    // for observeAsState()
    implementation(libs.compose.runtime.livedata)

    // room database
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)
}
```
Sync gradle.

# Room Database

- Create Database
  home/db/TodoDatabase.kt

```
package com.maronworks.roomdatabase.home.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.maronworks.roomdatabase.home.model.Todo

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    companion object {
        const val NAME = "todo_db"
    }

    abstract fun getTodoDao(): TodoDao
}
```

Create Model

```
package com.maronworks.roomdatabase.home.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val date: String
)
```

Create DAO

```
package com.maronworks.roomdatabase.home.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.maronworks.roomdatabase.home.model.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM TODO")
    fun getAllTodo() : LiveData<List<Todo>>

    @Insert
    fun addTodo(todo: Todo)

    @Upsert
    fun updateTodo(todo: Todo)

    @Query("DELETE FROM TODO WHERE id=:id")
    fun deleteTodo(id: Int)
}
```

Create an application [this will execute on app start]

```
package com.maronworks.roomdatabase.home

import android.app.Application
import androidx.room.Room
import com.maronworks.roomdatabase.home.db.TodoDatabase

class MainApplication : Application(){
    companion object{
        lateinit var todoDatabase: TodoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        todoDatabase = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            TodoDatabase.NAME
        ).build()
    }
}
```

Register the app in AndroidManifest.xml

```
      android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.RoomDatabase"
        android:name=".home.MainApplication"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
```

# Usage

Create a VewModel

```
package com.maronworks.roomdatabase.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maronworks.roomdatabase.home.model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private val todoDao = MainApplication.todoDatabase.getTodoDao()
    val todoList: LiveData<List<Todo>> = todoDao.getAllTodo()

    fun addTodo(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.addTodo(Todo(title = title, date = getCurrentDateTime()))
        }
    }

    fun updateTodo(id: Int, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateTodo(Todo(id = id, title = title, date = getCurrentDateTime()))
        }
    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
        }
    }

    private fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm:ss a")
        return currentDateTime.format(formatter)
    }
}
```

Sample UI

```
package com.maronworks.roomdatabase.home

...
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    val viewModel = HomeViewModel()
    val todoList by viewModel.todoList.observeAsState()
    var showAddDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showUpdateDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var id by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold(
        topBar = {
            ...
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = "New",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.W500
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = ""
                    )
                },
                onClick = { showAddDialog = !showAddDialog }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            todoList?.let { todos ->
                LazyColumn(
                    reverseLayout = true,
                    content = {
                        itemsIndexed(todos) { _: Int, item: Todo ->
                            TodoCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                todo = item,
                                onDelete = {
                                    viewModel.deleteTodo(item.id)
                                },
                                onClick = {
                                    title = item.title
                                    id = item.id
                                    showUpdateDialog = !showUpdateDialog
                                }
                            )
                        }
                    }
                )
            }
        }

        if (showAddDialog) {
            AddTodoDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = title,
                onValueChange = { title = it },
                onDismiss = {
                    showDialog = false
                    showAddDialog = false // hide dialog
                    viewModel.addTodo(title.trim()) // remove leading and trailing spaces
                    title = "" // clear title
                }
            )
        }

        if (showUpdateDialog) {
            UpdateTodoDialog(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = title,
                onValueChange = {
                    title = it
                },
                onDismiss = {
                    showUpdateDialog = false
                    viewModel.updateTodo(id = id, title = title.trim())
                    title = ""
                    id = 0
                }
            )
        }
    }
}
```

That's all. Live long and prosper
- Ralph Maron Eda
