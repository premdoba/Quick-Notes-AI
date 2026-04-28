package com.example.quicknotes

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quicknotes.ui.theme.QuickNotesTheme
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            QuickNotesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    "AI Study Buddy 🎓",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    StudyConverterScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

suspend fun extractPdfText(context: Context, uri: Uri): String {
    return withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return@withContext ""

        val reader = PdfReader(inputStream)
        val pdfDoc = PdfDocument(reader)

        val text = StringBuilder()

        for (i in 1..pdfDoc.numberOfPages) {
            val page = pdfDoc.getPage(i)
            text.append(PdfTextExtractor.getTextFromPage(page))
            text.append("\n\n")
        }

        pdfDoc.close()
        text.toString()
    }
}

@Composable
fun StudyConverterScreen(
    modifier: Modifier = Modifier,
    viewModel: StudyViewModel = viewModel()
) {
    var inputText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    // Store selected PDF Uri here
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }

    // PDF picker launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedPdfUri = uri
    }

    // Extract PDF text when URI changes
    LaunchedEffect(selectedPdfUri) {
        selectedPdfUri?.let { uri ->
            val pdfText = extractPdfText(context, uri)

            inputText = if (pdfText.isNotBlank()) {
                pdfText
            } else {
                "❌ Could not extract text from PDF (maybe scanned PDF)."
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Paste Lecture Content / Notes OR Upload PDF") },
            placeholder = { Text("Example: Photosynthesis explanation...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 6
        )

        Button(
            onClick = {
                pdfPickerLauncher.launch(arrayOf("application/pdf"))
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text("Upload PDF 📄")
        }

        Button(
            onClick = {
                viewModel.generateStudyMaterial(inputText)
            },
            enabled = inputText.isNotBlank() && uiState !is UiState.Loading,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Analyzing Content...")
            } else {
                Text("Generate Study Pack ✨")
            }
        }

        when (uiState) {
            is UiState.Success -> {
                val notes = (uiState as UiState.Success).notes
                StudyResultsView(notes)
            }

            is UiState.Error -> {
                val msg = (uiState as UiState.Error).message
                ErrorMessage(msg)
            }

            UiState.Idle -> {
                InfoPlaceholder()
            }

            UiState.Loading -> {
                // Do nothing
            }
        }
    }
}
@Composable
fun StudyResultsView(notes: StudyNotes) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ResultSection("📝 Short Notes", notes.shortNotes)
        ResultSection("❓ Important Questions", notes.importantQuestions)
        ResultSection("🎯 MCQs", notes.mcqs)
        ResultSection("🧠 Mindmap Summary", notes.mindmapSummary)
        ResultSection("📅 Revision Plan", notes.revisionPlan)
    }
}

@Composable
fun ResultSection(title: String, content: String) {
    if (content.isBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun InfoPlaceholder() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Input content to get:", fontWeight = FontWeight.Bold)
            BulletItem("Exam-focused short notes")
            BulletItem("High-probability questions")
            BulletItem("Practice MCQs")
            BulletItem("Revision roadmap")
        }
    }
}

@Composable
fun BulletItem(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
    )
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = "Oops! $message",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}