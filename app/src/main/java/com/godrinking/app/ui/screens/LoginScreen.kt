package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godrinking.app.data.UserRole
import com.godrinking.app.ui.theme.PrimaryRed
import com.godrinking.app.ui.theme.PrimaryRedDarker

@Composable
fun LoginScreen(onLogin: (UserRole) -> Unit) {
    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment  = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier            = Modifier.fillMaxWidth()
        ) {
            // ── Logo ──────────────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(88.dp)
                    .background(PrimaryRed, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍹", fontSize = 40.sp)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text       = "Go Drinking!",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text     = "Sistema de Gestão de Eventos",
                style    = MaterialTheme.typography.bodyMedium,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(Modifier.height(36.dp))

            // ── Card de login ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(20.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier              = Modifier.padding(24.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp),
                    horizontalAlignment   = Alignment.CenterHorizontally
                ) {
                    Text(
                        text      = "Entrar com Google",
                        style     = MaterialTheme.typography.titleMedium,
                        modifier  = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(4.dp))

                    GoogleSignInButton(
                        label   = "Entrar como Gerente",
                        onClick = { onLogin(UserRole.GERENTE) }
                    )

                    GoogleSignInButton(
                        label   = "Entrar como Dev",
                        onClick = { onLogin(UserRole.DEV) }
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text      = "Versão 1.0.0 – Protótipo Visual",
                        style     = MaterialTheme.typography.labelSmall,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(label: String, onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape    = RoundedCornerShape(10.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor   = Color(0xFF1F2937)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ícone Google simplificado (use vetor SVG real no projeto final)
            Surface(
                modifier = Modifier.size(20.dp),
                shape    = RoundedCornerShape(4.dp),
                color    = Color(0xFF4285F4)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text       = "G",
                        color      = Color.White,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text       = label,
                color      = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium,
                fontSize   = 15.sp
            )
        }
    }
}
