package com.example.balanja.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.balanja.ui.theme.BalanjaColor
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Initialize osmdroid configuration
    remember {
        val config = Configuration.getInstance()
        config.load(context, context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
        config.userAgentValue = context.packageName
        // Set cache paths to internal storage to avoid permission issues and storage lags
        val basePath = java.io.File(context.cacheDir, "osmdroid")
        basePath.mkdirs()
        config.osmdroidBasePath = basePath
        val tileCache = java.io.File(config.osmdroidBasePath, "tiles")
        tileCache.mkdirs()
        config.osmdroidTileCache = tileCache
        true
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
            // Default location to ULM Banjarmasin approx: -3.298, 114.587
            val ulmGeoPoint = GeoPoint(-3.298, 114.587)
            controller.setCenter(ulmGeoPoint)
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Peta Lokasi Pedagang", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF9F8))
            )
        },
        containerColor = Color(0xFFFBF9F8)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = BalanjaColor.Primary)
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        view.overlays.removeAll { it is Marker }
                        uiState.stalls.forEach { stall ->
                            if (stall.latitude != 0.0 && stall.longitude != 0.0) {
                                val marker = Marker(view)
                                marker.position = GeoPoint(stall.latitude, stall.longitude)
                                marker.title = stall.name
                                marker.snippet = stall.location
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                view.overlays.add(marker)
                            }
                        }

                        // Center on selected stall if it has valid coordinates
                        val selectedStall = uiState.selectedStallLocation
                        if (selectedStall != null && selectedStall.latitude != 0.0 && selectedStall.longitude != 0.0) {
                            view.controller.animateTo(GeoPoint(selectedStall.latitude, selectedStall.longitude), 18.0, 1000L)
                        }

                        view.invalidate()
                    }
                )
            }
        }
    }
}
