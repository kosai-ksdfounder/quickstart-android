package com.google.firebase.example.dataconnect.ui.components

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


@Composable
fun ReviewCard(
    userName: String,
    date: Date,
    rating: Double,
    text: String,
    movieName: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = if (movieName != null) {
                    userName + " on " + movieName
                } else {
                    userName
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Row(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = SimpleDateFormat(
                        "dd MMM, yyyy",
                        Locale.getDefault()
                    ).format(date),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rating: ",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$rating",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
