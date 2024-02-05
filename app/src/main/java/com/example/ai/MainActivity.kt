package com.example.ai

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ai.ui.theme.AITheme
import com.example.ai.ui.theme.green
import com.google.ai.client.generativeai.GenerativeModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = R.color.liteBlack)),
                    color = colorResource(id = R.color.liteBlack)
                ) {
                    Home()
                }
            }
        }
    }
}

@Composable
fun Home() {
    TopAppBar()
}


@Composable
fun TopAppBar() {

    var selected by remember {
        mutableStateOf(true)
    }

    Scaffold(
        topBar =
        {
            Surface(
                color = colorResource(id = R.color.liteBlack),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    AnalyseTextBox(
                        modifier = Modifier
                            .weight(1f)
                            .clip(
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable {
                                selected = true
                            }
                            .height(50.dp)
                            .background(
                                colorResource(id = R.color.liteBlack)
                            ),
                        selected
                    )

                    AnalyseImageBox(
                        modifier = Modifier
                            .weight(1f)
                            .clip(
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable {
                                selected = false
                            }
                            .height(50.dp)
                            .background(
                                colorResource(id = R.color.liteBlack)
                            ),
                        selected
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            color = colorResource(id = R.color.liteBlack)
        ) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                AnimatedVisibility(
                    visible = selected,
                    enter = slideInHorizontally(animationSpec = tween(durationMillis = 300)) {
                        it / 2
                    } + fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    ),
                    exit = slideOutHorizontally(animationSpec = tween(300)) {
                        it / 2
                    } + fadeOut()

                ) {
                    AnalyseTextPage()
                }


                AnimatedVisibility(
                    visible = !selected,
                    enter = slideInHorizontally(animationSpec = tween(durationMillis = 400)) {
                        it / 2
                    } + fadeIn(animationSpec = tween(durationMillis = 400)),
                    exit = slideOutHorizontally(animationSpec = tween(300)) {
                        it / 2
                    } + fadeOut()

                ) {
                    AnalyseImagePage()
                }
            }


        }
    }
}

@Composable
fun AnalyseTextBox(modifier: Modifier , selected: Boolean) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = "Analyse Text",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            color = if(selected) colorResource(id = R.color.green) else colorResource(id = R.color.white)
        )
    }
}

@Composable
fun AnalyseImageBox(modifier: Modifier , selected: Boolean) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = "Analyse Image",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            color= if(!selected) colorResource(id = R.color.green) else colorResource(id = R.color.white)
        )
    }
}

@Composable
fun AnalyseTextPage() {
    val model = GenerativeModel(
        // For text-only input, use the gemini-pro model
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = "AIzaSyDQishsLb0LfSnITmmIw9ldz_QWZ8XrZF0"
    )

    var processing by remember {
        mutableStateOf(false)
    }

    val responseModel: AIModel = viewModel(AIModel::class.java)
    val response by responseModel.mutable.collectAsState()
    val loading by responseModel.isLoading.collectAsState()



    Column(
        modifier = Modifier
            .fillMaxSize()

    ) {
            if (processing) {
                AnimatedContent(
                    label = "animated Content",
                    targetState = loading,
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(200)
                        ) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (it) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        )
                        {
                            CircularProgressIndicator(
                                color = colorResource(id =R.color.green),
                                modifier = Modifier
                                    .width(64.dp)
                            )
                        }

                    } else {
                        DisplayTextScreen(
                            response,
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(
                                    rememberScrollState()
                                )
                        )
                    }
                }
            } else {
                Surface(
                    color= colorResource(id = R.color.liteBlack),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {}

            }


        QuestionSection{
            responseModel.getResponse(model, it)
            processing = true
        }
    }
}

@Composable
fun AnalyseImagePage() {
    var imageURI by remember {
        mutableStateOf<Uri?>(null)
    }

    var processing by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val pickedImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            imageURI = it
        }
    )


    val model = GenerativeModel(
        // For text-only input, use the gemini-pro model
        modelName = "gemini-pro-vision",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = "AIzaSyDQishsLb0LfSnITmmIw9ldz_QWZ8XrZF0"
    )

    val responseModel: AIModel = viewModel(AIModel::class.java)
    val response by responseModel.mutableImage.collectAsState()
    val loading by responseModel.isLoading.collectAsState()


    Column(modifier = Modifier.fillMaxSize()) {
        DisplayImageScreen(
            response,
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                ),
            {
                pickedImage.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            imageURI,
            processing,
            loading
        )

        QuestionSection {
            responseModel.getImageResponse(model, it, imageURI, context)
            processing = true
        }
    }
}


@Composable
fun QuestionSection(
    onClick: (request: String) -> Unit
) {
    var question by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = question,
            modifier = Modifier
                .padding(vertical = 15.dp, horizontal = 8.dp)
                .weight(1f)
                .clip(
                    shape = RoundedCornerShape(7.dp)
                )
                .background(colorResource(id = R.color.green)),
            onValueChange = {
                question = it
            })

        IconButton(
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 8.dp),
            onClick = {
                onClick(question)
                question = ""
            }
        ) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.baseline_send_24),
                contentDescription = null,

                )
        }
    }

}


@Composable
fun DisplayTextScreen(response: String, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = response,
            modifier = Modifier
                .padding(10.dp),
            colorResource(id = R.color.green)
        )
    }
}

@Composable
fun DisplayImageScreen(
    response: String,
    modifier: Modifier,
    onClick: () -> Unit,
    imageURI: Uri?,
    processing: Boolean,
    loading:Boolean
) {


    Column(
        modifier = modifier
    ) {
        if (imageURI == null) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_photo_size_select_actual_24),
                contentDescription = "",
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        onClick()
                    }
            )
        } else {
            AsyncImage(
                model = imageURI,
                contentDescription = null,
                modifier = Modifier
                    .size(350.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        onClick()
                    }
            )
        }
        if (processing) {
            AnimatedContent(
                label = "animated Content",
                targetState = loading,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(200)
                    ) togetherWith fadeOut(animationSpec = tween(200))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                if (it) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    )
                    {
                        CircularProgressIndicator(
                            color = colorResource(id = R.color.green),
                            modifier = Modifier
                                .width(64.dp)
                        )
                    }
                } else {
                    Text(
                        text = response,
                        modifier = Modifier
                            .padding(10.dp),
                        color = colorResource(id = R.color.green)
                    )
                }
            }
        } else {
            Surface(
                color = colorResource(id = R.color.liteBlack),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {}
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuestionSectionPreview() {
    AITheme {
        QuestionSection {}
    }
}

