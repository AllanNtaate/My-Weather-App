package com.weatherapp.presentation.ui

import android.Manifest
import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.weatherapp.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    requestLocationPermissionLauncher: (String) -> Unit,
    context: Context
) {
    var cityName by remember { mutableStateOf("") }
    val weatherResponse by weatherViewModel.weatherResponse
    val isLoading by weatherViewModel.isLoading
    val errorMessage by weatherViewModel.errorMessage

    // Show a Toast if there is an error message
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("My Weather App", modifier = Modifier.fillMaxWidth(), fontSize = 16.sp, fontFamily = FontFamily(
            Typeface.DEFAULT_BOLD), color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = cityName,
            onValueChange = { newCityName -> cityName = newCityName },
            label = { Text("Enter US city name") },
            modifier =  Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (cityName.isNotEmpty()) {
                weatherViewModel.getWeatherByCity(cityName)
            }
        }) {
            Text("Get Weather by City")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            requestLocationPermissionLauncher(Manifest.permission.ACCESS_FINE_LOCATION)
        }) {
            Text("Use Current Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            weatherResponse?.let { weather ->

                WeatherDetailText("City: ",weather.name.toString())
                WeatherDetailText(label = "Temperature: ", value = "${weather.main?.temp ?: "N/A"} °F")
                WeatherDetailText(label = "Humidity: ", value = "${weather.main?.humidity ?: "N/A"} %")
                WeatherDetailText(label = "Weather: ", value = weather.weather?.firstOrNull()?.description ?: "N/A")
                WeatherDetailText(label = "Wind Speed: ", value = "${weather.wind?.speed ?: "N/A"}")
                WeatherDetailText(label = "Wind Degree: ", value = "${weather.wind?.deg ?: "N/A"}")
                WeatherDetailText(label = "Pressure: ", value = "${weather.main?.pressure ?: "N/A"}")

                weather.weather?.get(0)?.icon?.let { icon ->
                    Icon(painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${icon}@4x.png"), modifier = Modifier.size(50.dp), contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun WeatherDetailText(label: String, value: String) {
    Text(
        text = buildAnnotatedString {
            append(label)
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(value)
            }
        }
    )
}

