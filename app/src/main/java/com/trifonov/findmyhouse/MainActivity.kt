package com.trifonov.findmyhouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.trifonov.findmyhouse.data.Address
import com.trifonov.findmyhouse.data.KtorClient
import com.trifonov.findmyhouse.ui.theme.FindMyHouseTheme
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        setContent {
            FindMyHouseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {

    val paris = LatLng(48.868546, 2.33031)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(paris, 10f)
    }
    val markerState = rememberMarkerState(position = paris)

    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var items  by remember { mutableStateOf( emptyList<Address>()) }


    val searchBarPadding: Dp by animateDpAsState(
        if (!active) {
            8.dp
        } else {
            0.dp
        }
    )

    if(text.isNotEmpty() && text.length >= 3) {
        LaunchedEffect(key1 = text) {
            launch {
                val ktorClient = KtorClient()
                val result = ktorClient.getHouseAdresess(text)
                val adresses = result.features
                    ?.filter { it.properties.type == "housenumber" }
                    ?.take(5)
                    ?.map {
                       Address( street = it.properties.street!!,
                           houseNumber = it.properties.housenumber!!,
                           location = it.geometry )
                    }

                if (adresses != null) {
                    items = adresses
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            modifier = Modifier.padding(horizontal = searchBarPadding),
            query = text,
            onQueryChange = { text = it },
            onSearch = {
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = {
                Text("Search")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            },
            trailingIcon = {
                if (active) {
                    Icon(
                        modifier = Modifier.clickable {
                            if (text.isNotEmpty()) {
                                text = ""
                            } else {
                                active = false
                            }
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon"
                    )
                }
            }
        ) {
            items.forEach {
                Row(modifier = Modifier
                    .clickable {
                        text = it.street + " " +  it.houseNumber
                        active = false
                        val location = LatLng(it.location.coordinates[1], it.location.coordinates[0])
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 10f)
                        markerState.position = location
                    }
                    .fillMaxWidth()
                    .padding(all = 12.dp)) {
                    Icon(
                        modifier = Modifier.padding(end = 10.dp),
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Icon"
                    )
                    Text(text = it.street)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = it.houseNumber)
                }
            }
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(state = markerState)
        }
    }
}