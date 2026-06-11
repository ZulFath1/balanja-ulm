package com.example.balanja.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.balanja.ui.theme.BalanjaColor

/**
 * Reusable TextField component sesuai Balanja design guideline
 * 
 * Features:
 * - Error message display bawah field
 * - Helper text (misal: "GUNAKAN EMAIL INSTITUSIONAL")
 * - Password toggle visibility
 * - Leading icon
 */
@Composable
fun BalanjaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperText: String? = null,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Default,
    onImeAction: () -> Unit = {},
    enabled: Boolean = true
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = label,
                    tint = if (isError) BalanjaColor.Danger else BalanjaColor.TextSecondary
                )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = BalanjaColor.TextSecondary
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
//            colors = TextFieldDefaults.colors(
//                focusedBorderColor = BalanjaColor.Primary,
//                unfocusedBorderColor = BalanjaColor.Border,
//                errorBorderColor = BalanjaColor.Danger,
//                focusedLabelColor = BalanjaColor.Primary,
//                errorLabelColor = BalanjaColor.Danger
//            ),
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = KeyboardActions(onDone = { onImeAction() }),
            enabled = enabled
        )
        
        // Helper text (emas, uppercase)
        if (helperText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = helperText.uppercase(),
                fontSize = 11.sp,
                color = BalanjaColor.GoldLabel,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = BalanjaColor.Danger,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
