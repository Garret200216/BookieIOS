package com.bookie.ios

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Slate = Color(0xFF445C6A)
private val Cream = Color(0xFFF4F3EF)
private val CardGray = Color(0xFFE7E6E1)
private val DarkText = Color(0xFF22252A)
private val Muted = DarkText.copy(alpha = 0.72f)
private val DashboardHeader = Color(0xFF475A67)
private val DashboardBackground = Color(0xFFF4F5F4)
private val DashboardCard = Color(0xFFE5E4DF)
private val DashboardShortcut = Color(0xFFACBAC1)
private val EmergencyRed = Color(0xFFCF4A44)
private val DashboardText = Color(0xFF292F32)

private data class Book(val id: Int, val title: String, val author: String, val genre: String)
private val genres = listOf(
    "Gothic & Psychological Horror",
    "Classic Fantasy & Children Literature",
    "Epic Quest",
    "Tragedy"
)
private fun genreLabel(genre: String) = if (genre == genres[1]) "Fantasy & Children's Classics" else genre
private val books = listOf(
    Book(1, "The Adventures of Sherlock Holmes", "Arthur Conan Doyle", genres[3]),
    Book(2, "Dracula", "Bram Stoker", genres[0]),
    Book(3, "Frankenstein", "Mary Wollstonecraft Shelley", genres[0]),
    Book(4, "Alice's Adventures in Wonderland", "Lewis Carroll", genres[1]),
    Book(5, "Moby Dick; or The Whale", "Herman Melville", genres[2]),
    Book(6, "Romeo and Juliet", "William Shakespeare", genres[3]),
    Book(7, "The Adventures of Tom Sawyer", "Mark Twain", genres[2]),
    Book(8, "The Yellow Wallpaper", "Charlotte Perkins Gilman", genres[0]),
    Book(9, "A Christmas Carol", "Charles Dickens", genres[3]),
    Book(10, "The Wonderful Wizard of Oz", "L. Frank Baum", genres[1]),
    Book(11, "Winnie-the-Pooh", "A. A. Milne", genres[1]),
    Book(12, "Les Misérables", "Victor Hugo", genres[3]),
    Book(13, "The King in Yellow", "Robert W. Chambers", genres[0]),
    Book(14, "The Odyssey", "Homer", genres[2]),
    Book(15, "The Great Gatsby", "F. Scott Fitzgerald", genres[3])
)

@Composable
fun App() {
    var screen by remember { mutableStateOf("library") }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var lastBook by remember { mutableStateOf<Book?>(null) }
    var page by remember { mutableIntStateOf(1) }
    var pin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    var feature by remember { mutableStateOf("") }
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Cream).safeDrawingPadding()) {
            when (screen) {
                "library" -> LibraryScreen(
                    lastBook = lastBook,
                    onOpenBook = { book -> selectedBook = book; lastBook = book; page = 1; screen = "reader" },
                    onAccount = { feature = "Account"; screen = "feature" }
                )
                "reader" -> ReaderScreen(
                    selectedBook, page,
                    onBack = { screen = "library" },
                    onPrevious = { if (page > 1) page-- },
                    onNext = { page++ },
                    onPage = { pin = ""; pinError = false; screen = "pin" }
                )
                "pin" -> Column(Modifier.fillMaxSize().background(Cream)) {
                    SimpleHeader("Bookie")
                    Text("Enter Passcode", Modifier.padding(16.dp), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) { pin = it; pinError = false } },
                        label = { Text("4-digit PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    if (pinError) Text("Incorrect PIN", Modifier.padding(16.dp), color = EmergencyRed)
                    Button(onClick = { if (pin == "0000") screen = "dashboard" else pinError = true }, modifier = Modifier.padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Slate)) { Text("Unlock") }
                    TextButton(onClick = { screen = "reader" }) { Text("Cancel", color = Slate) }
                }
                "dashboard" -> DashboardScreen(
                    onFeature = { feature = it; screen = "feature" },
                    onLibrary = { screen = "library"; pin = "" }
                )
                "feature" -> Column(Modifier.fillMaxSize().background(DashboardBackground)) {
                    SimpleHeader("Bookie", DashboardHeader)
                    TextButton(onClick = { screen = if (feature == "Account") "library" else "dashboard" }) { Text("← Back", color = Slate) }
                    Text(feature, Modifier.padding(20.dp), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("This feature has not yet been ported.", Modifier.padding(horizontal = 20.dp))
                }
            }
        }
    }
}

@Composable
private fun SimpleHeader(title: String, background: Color = Slate, onBack: (() -> Unit)? = null, onAccount: (() -> Unit)? = null) {
    Box(Modifier.fillMaxWidth().height(64.dp).background(background)) {
        Text(title, Modifier.align(Alignment.Center), fontFamily = FontFamily.Cursive, fontSize = 38.sp, color = Color.White)
        if (onBack != null) TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) { Text("←", color = Color.White, fontSize = 25.sp) }
        if (onAccount != null) TextButton(onClick = onAccount, modifier = Modifier.align(Alignment.CenterEnd)) { Text("●", color = Color.White, fontSize = 22.sp) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(lastBook: Book?, onOpenBook: (Book) -> Unit, onAccount: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var openGenre by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(search) { if (search.trim() == "0000") { search = ""; onAccount() } }
    if (openGenre != null) {
        GenreGrid(openGenre!!, onGenreChange = { openGenre = it }, onBack = { openGenre = null }, onOpenBook = onOpenBook)
        return
    }
    Column(Modifier.fillMaxSize().background(Cream)) {
        SimpleHeader("Bookie", onAccount = onAccount)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = search, onValueChange = { search = it },
                    placeholder = { Text("Search books or authors") },
                    leadingIcon = { Text("⌕", fontSize = 27.sp) },
                    trailingIcon = { if (search.isNotEmpty()) IconButton(onClick = { search = "" }) { Text("×", fontSize = 24.sp) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Slate, unfocusedBorderColor = Color.Gray,
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                    )
                )
            }
            if (search.isNotBlank()) {
                val results = books.filter { it.title.contains(search, true) || it.author.contains(search, true) }
                item {
                    Text(
                        when (results.size) { 0 -> "No books found. Try another title or author."; 1 -> "Search results · 1 book"; else -> "Search results · ${results.size} books" },
                        Modifier.padding(horizontal = 16.dp, vertical = 4.dp), fontSize = 15.sp, color = Muted
                    )
                }
                items(results, key = { it.id }) { book ->
                    Box(Modifier.padding(horizontal = 16.dp)) { SearchBookRow(book) { onOpenBook(book) } }
                }
            } else {
                item { ContinueReadingCard(lastBook ?: books.first(), lastBook != null) { onOpenBook(lastBook ?: books.first()) } }
                items(genres) { genre ->
                    GenreShelf(genre, books.filter { it.genre == genre }, onSeeAll = { openGenre = genre }, onOpenBook = onOpenBook)
                }
            }
        }
    }
}

@Composable
private fun BlankCover(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .shadow(3.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
    )
}

@Composable
private fun ContinueReadingCard(book: Book, hasProgress: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), color = CardGray) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            BlankCover(Modifier.width(56.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(if (hasProgress) "Continue reading" else "Start reading", fontSize = 13.sp, color = Muted)
                Text(book.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(book.author, fontSize = 13.sp, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(22.dp), color = Slate) {
                Text(if (hasProgress) "Resume" else "Start", Modifier.padding(horizontal = 18.dp, vertical = 12.dp), color = Color.White, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun GenreShelf(genre: String, shelfBooks: List<Book>, onSeeAll: () -> Unit, onOpenBook: (Book) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(genreLabel(genre), Modifier.weight(1f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText, maxLines = 2, overflow = TextOverflow.Ellipsis)
            TextButton(onClick = onSeeAll) { Text("See all", color = Slate, fontWeight = FontWeight.SemiBold) }
        }
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(shelfBooks, key = { it.id }) { book ->
                BookCoverCard(book, Modifier.width(108.dp)) { onOpenBook(book) }
            }
        }
    }
}

@Composable
private fun BookCoverCard(book: Book, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier.clickable { onClick() }, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BlankCover(Modifier.fillMaxWidth())
        Text(book.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DarkText, maxLines = 2, minLines = 2, overflow = TextOverflow.Ellipsis)
        Text(book.author, fontSize = 12.sp, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun GenreGrid(genre: String, onGenreChange: (String) -> Unit, onBack: () -> Unit, onOpenBook: (Book) -> Unit) {
    val genreBooks = books.filter { it.genre == genre }
    Column(Modifier.fillMaxSize().background(Cream)) {
        SimpleHeader("Bookie", onBack = onBack)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(genreLabel(genre), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = DarkText)
                    Text(if (genreBooks.size == 1) "1 book" else "${genreBooks.size} books", fontSize = 14.sp, color = Muted)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(genres) { chipGenre ->
                            FilterChip(
                                selected = chipGenre == genre, onClick = { onGenreChange(chipGenre) },
                                label = { Text(genreLabel(chipGenre), maxLines = 1) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Slate, selectedLabelColor = Color.White)
                            )
                        }
                    }
                }
            }
            gridItems(genreBooks, key = { it.id }) { book ->
                BookCoverCard(book, Modifier.fillMaxWidth()) { onOpenBook(book) }
            }
        }
    }
}

@Composable
private fun SearchBookRow(book: Book, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 84.dp), color = CardGray, shape = RoundedCornerShape(10.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            BlankCover(Modifier.width(46.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(book.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DarkText, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(book.author, fontSize = 13.sp, color = Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("›", fontSize = 26.sp, color = Muted)
        }
    }
}

@Composable
private fun ReaderScreen(book: Book?, page: Int, onBack: () -> Unit, onPrevious: () -> Unit, onNext: () -> Unit, onPage: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Cream)) {
        SimpleHeader("Bookie", onBack = onBack)
        if (book != null) Text(book.title, Modifier.padding(16.dp), fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Box(Modifier.weight(1f).fillMaxWidth().padding(16.dp).background(Color.White))
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onPrevious, enabled = page > 1, colors = ButtonDefaults.buttonColors(containerColor = Slate)) { Text("Previous") }
            Text("$page", Modifier.clickable { onPage() }.padding(8.dp), fontSize = 20.sp)
            Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Slate)) { Text("Next") }
        }
    }
}

private data class Stat(val id: String, val label: String, val value: String, val wide: Boolean = false)
private data class Shortcut(val id: String, val label: String, val emergency: Boolean = false)
private val stats = listOf(
    Stat("books", "Books read", "0"), Stat("photos", "Photos saved", "0"),
    Stat("money", "Money saved", "$0.00", true), Stat("notes", "Notes written", "0"),
    Stat("documents", "Documents submitted", "0"), Stat("contacts", "Trusted contacts", "0", true)
)
private val shortcuts = listOf(
    Shortcut("resources", "Resources"), Shortcut("vault", "Vault"), Shortcut("contacts", "Trusted Contacts"),
    Shortcut("messages", "Messages"), Shortcut("notes", "Notes"), Shortcut("documents", "Documents"),
    Shortcut("emergency", "Emergency", true), Shortcut("qrcode", "Share Bookie")
)

@Composable
private fun DashboardScreen(onFeature: (String) -> Unit, onLibrary: () -> Unit) {
    var shownStats by remember { mutableStateOf(setOf("books", "photos", "money")) }
    var shownShortcuts by remember { mutableStateOf(setOf("resources", "vault", "contacts", "messages", "qrcode", "emergency")) }
    var picker by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().background(DashboardBackground)) {
        SimpleHeader("Bookie", DashboardHeader, onAccount = { onFeature("Settings") })
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)) {
            DashboardSection("My Dashboard", "Edit", true) { picker = "stats" }
            Spacer(Modifier.height(12.dp))
            val visibleStats = stats.filter { it.id in shownStats }
            if (visibleStats.isEmpty()) Text("No cards shown. Tap Edit to choose some.")
            val rows = mutableListOf<List<Stat>>()
            var pending: Stat? = null
            visibleStats.forEach { stat ->
                if (stat.wide) { pending?.let { rows.add(listOf(it)) }; pending = null; rows.add(listOf(stat)) }
                else if (pending == null) pending = stat
                else { rows.add(listOf(pending!!, stat)); pending = null }
            }
            pending?.let { rows.add(listOf(it)) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { stat ->
                            Card(Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = DashboardCard)) {
                                Column(Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(stat.value, fontFamily = FontFamily.Serif, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = DashboardText)
                                    Text(stat.label, fontSize = 14.sp, color = DashboardText, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
            DashboardSection("Quick Access", "+ Add", false) { picker = "shortcuts" }
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                shortcuts.filter { it.id in shownShortcuts }.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { shortcut ->
                            Button(
                                onClick = { onFeature(shortcut.label) }, modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if (shortcut.emergency) EmergencyRed else DashboardShortcut, contentColor = DashboardText)
                            ) { Text(if (shortcut.emergency) shortcut.label.uppercase() else shortcut.label, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center) }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onLibrary, modifier = Modifier.fillMaxWidth()) { Text("Return to Library", color = DashboardText) }
        }
    }
    if (picker != null) {
        val isStats = picker == "stats"
        val options = if (isStats) stats.map { it.id to it.label } else shortcuts.map { it.id to it.label }
        var selected by remember(picker) { mutableStateOf(if (isStats) shownStats else shownShortcuts) }
        AlertDialog(
            onDismissRequest = { picker = null },
            title = { Text(if (isStats) "Choose dashboard cards" else "Choose quick access") },
            text = {
                Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                    options.forEach { (id, label) ->
                        Row(Modifier.fillMaxWidth().clickable { selected = if (id in selected) selected - id else selected + id }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = id in selected, onCheckedChange = { selected = if (it) selected + id else selected - id })
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { if (isStats) shownStats = selected else shownShortcuts = selected; picker = null }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { picker = null }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun DashboardSection(title: String, action: String, large: Boolean, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = if (large) 28.sp else 22.sp, color = DashboardText)
        TextButton(onClick = onAction) { Text(action, color = DashboardText, fontWeight = FontWeight.SemiBold) }
    }
}
