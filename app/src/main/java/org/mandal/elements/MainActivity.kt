package org.mandal.elements

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Navy = Color(0xFF07111F)
private val Panel = Color(0xFF102338)
private val Cyan = Color(0xFF55E6EF)
private val Muted = Color(0xFF9AB0C8)
private val Ink = Color(0xFFEDF6FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7, 17, 31)
        window.navigationBarColor = android.graphics.Color.rgb(7, 17, 31)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Cyan, onPrimary = Navy, background = Navy, surface = Panel, onSurface = Ink, onBackground = Ink, secondary = Color(0xFF9A8CFF))) {
                NativeMandal()
            }
        }
    }
}

@Composable
private fun NativeMandal() {
    var tab by remember { mutableIntStateOf(0) }
    var selectedNumber by remember { mutableIntStateOf(1) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("همه") }
    var first by remember { mutableIntStateOf(1) }
    var second by remember { mutableIntStateOf(8) }
    var third by remember { mutableIntStateOf(0) }
    val selected = periodicElements.first { it.number == selectedNumber }

    Scaffold(containerColor = Navy, bottomBar = {
        NavigationBar(containerColor = Panel) {
            listOf("جدول", "عنصر", "آزمایشگاه", "تحلیل").forEachIndexed { index, title ->
                NavigationBarItem(selected = tab == index, onClick = { tab = index },
                    icon = { Text(listOf("▦", "◉", "✦", "⌁")[index], color = Cyan, fontSize = 20.sp) }, label = { Text(title) })
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(46.dp).background(Brush.linearGradient(listOf(Cyan, Color(0xFF9A8CFF))), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Text("M", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("MANDAL / ELEMENTS", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                    Text("آزمایشگاه بومی عناصر · Native Android", fontSize = 12.sp, color = Muted)
                }
            }
            DashboardHero()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Metric("118", "عنصر")
                Metric("18 / 7", "گروه / دوره")
                Metric("s · p · d · f", "بلوک‌ها")
            }
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("روندهای تناوبی", "اتم‌های درخشان", "مدل مولکولی", "کاربردهای واقعی").forEachIndexed { i, label ->
                    Surface(color = if (i == 0) Color(0xFF123B4B) else Panel, shape = RoundedCornerShape(10.dp), border = BorderStroke(1.dp, Cyan.copy(alpha = 0.22f))) {
                        Text(label, Modifier.padding(horizontal = 10.dp, vertical = 8.dp), color = if (i == 0) Cyan else Muted, fontSize = 11.sp)
                    }
                }
            }
            when (tab) {
                0 -> {
                    Text("جدول تناوبی · ۱۸ گروه / ۷ دوره", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    BasicTextField(value = query, onValueChange = { query = it }, singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Ink, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(12.dp)).padding(12.dp),
                        decorationBox = { inner -> Box { if (query.isEmpty()) Text("جست‌وجوی نام، نماد یا عدد اتمی…", color = Muted); inner() } })
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        listOf("همه", "نافلز", "فلز قلیایی", "فلز قلیایی خاکی", "فلز واسطه", "گاز نجیب", "هالوژن", "شبه‌فلز", "لانتانید", "اکتینید").forEach {
                            OutlinedButton(onClick = { category = it }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp)) {
                                Text(it, fontSize = 10.sp, color = if (category == it) Cyan else Muted)
                            }
                        }
                    }
                    val filtered = periodicElements.filter { e ->
                        (category == "همه" || e.categoryName == category) &&
                        (query.isBlank() || e.symbol.contains(query, true) || e.persianName.contains(query, true) ||
                            e.englishName.contains(query, true) || e.number.toString().contains(query))
                    }
                    Text(filtered.size.toString() + " عنصر", color = Muted, fontSize = 12.sp)
                    LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(filtered, key = { it.number }) { e ->
                            ElementTile(e, selectedNumber == e.number) { selectedNumber = e.number; tab = 1 }
                        }
                    }
                }
                1 -> {
                    Text("شناسنامهٔ عنصر · مدل اتمی متحرک", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Column(Modifier.fillMaxWidth().weight(1f).background(Panel, RoundedCornerShape(18.dp)).padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(selected.number.toString(), color = Muted)
                                Text(selected.symbol, fontSize = 68.sp, fontWeight = FontWeight.Black, color = Cyan)
                                Text(selected.persianName, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                                Text(selected.englishName, color = Muted)
                                Text(selected.categoryName, color = Color(0xFFFFCE70), modifier = Modifier.padding(top = 6.dp))
                            }
                            NativeAtom(selected)
                        }
                        HorizontalDivider(color = Muted.copy(alpha = 0.25f))
                        InfoRow("عدد اتمی", selected.number.toString())
                        InfoRow("جرم اتمی (u)", String.format(Locale.US, "%.4f", selected.mass))
                        InfoRow("دوره", selected.period.toString())
                        InfoRow("گروه", selected.group.toString())
                        Spacer(Modifier.height(12.dp))
                        Text("مدل اتمی شماتیک و آموزشی است؛ مکان کوانتومی الکترون‌ها را نمایش نمی‌دهد.", color = Muted, fontSize = 12.sp)
                        Spacer(Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { selectedNumber = if (selectedNumber == 1) 118 else selectedNumber - 1 }) { Text("قبلی") }
                            Button(onClick = { selectedNumber = if (selectedNumber == 118) 1 else selectedNumber + 1 }) { Text("بعدی") }
                        }
                    }
                }
                2 -> {
                    Text("آزمایشگاه پیوندها · مولکول‌نما", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("نمونه‌های ثبت‌شده از ترکیب‌های شناخته‌شده؛ این ابزار پیش‌بینی واکنش نیست.", color = Muted, fontSize = 12.sp)
                    Column(Modifier.fillMaxWidth().weight(1f).background(Panel, RoundedCornerShape(18.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        ElementPicker("عنصر اول", first) { first = it }
                        ElementPicker("عنصر دوم", second) { second = it }
                        ElementPicker("عنصر سوم (اختیاری)", third) { third = it }
                        val picked = listOf(first, second) + if (third == 0) emptyList() else listOf(third)
                        val found = knownCompounds.filter { it.numbers.distinct().sorted() == picked.distinct().sorted() }
                        NativeMolecule(picked.mapNotNull { n -> periodicElements.find { it.number == n } })
                        if (found.isEmpty()) {
                            Text("برای این انتخاب، ترکیبی در فهرست محدود آموزشی ثبت نشده است؛ این به معنی ناممکن بودن واکنش نیست.", color = Color(0xFFFFCE70), fontSize = 13.sp)
                        } else found.forEach { c ->
                            Card(colors = CardDefaults.cardColors(containerColor = Navy)) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(c.formula, fontSize = 28.sp, color = Cyan, fontWeight = FontWeight.Bold)
                                    Text(c.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(c.description, color = Muted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                else -> { TrendDashboard() }
            }
        }
    }
}

@Composable
private fun TrendDashboard() {
    val scroll = rememberScrollState()
    Column(Modifier.fillMaxSize().verticalScroll(scroll), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("نقشهٔ روندهای تناوبی", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
        Text("نمای آموزشی از الگوهای عمومی جدول؛ مقدار دقیق هر عنصر را از شناسنامهٔ آن بررسی کن.", color = Muted, fontSize = 12.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Panel), border = BorderStroke(1.dp, Cyan.copy(alpha = .25f))) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("روندهای اصلی", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                TrendLine("شعاع اتمی", "افزایش به سمت چپ و پایین", "←  ↓", Color(0xFF8C7BFF))
                TrendLine("انرژی یونش", "افزایش به سمت راست و بالا", "→  ↑", Color(0xFF55E6EF))
                TrendLine("الکترونگاتیوی", "معمولاً افزایش به سمت راست و بالا", "→  ↑", Color(0xFFFFC96B))
                TrendLine("خاصیت فلزی", "افزایش به سمت چپ و پایین", "←  ↓", Color(0xFF79F2B3))
                Text("استثناهای شیمیایی وجود دارند؛ این پیکان‌ها روند کلی‌اند، نه دادهٔ عددی برای تک‌تک عناصر.", color = Muted, fontSize = 11.sp)
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("راهنمای خانواده‌های عناصر", color = Cyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                val legend = listOf(
                    Triple("فلز قلیایی", Color(0xFF9D3348), "گروه ۱"),
                    Triple("فلز قلیایی خاکی", Color(0xFFB56A28), "گروه ۲"),
                    Triple("فلز واسطه", Color(0xFF155DB5), "بلوک d"),
                    Triple("فلز پس‌واسطه", Color(0xFF168A83), "فلزها"),
                    Triple("شبه‌فلز", Color(0xFF479A54), "مرز فلز/نافلز"),
                    Triple("نافلز", Color(0xFF248C78), "بلوک p و H"),
                    Triple("هالوژن", Color(0xFF8753C5), "گروه ۱۷"),
                    Triple("گاز نجیب", Color(0xFF6946BC), "گروه ۱۸"),
                    Triple("لانتانید", Color(0xFFB24D9C), "ردیف f"),
                    Triple("اکتینید", Color(0xFF80475E), "ردیف f")
                )
                legend.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { item ->
                            Row(Modifier.weight(1f).background(Navy, RoundedCornerShape(10.dp)).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(11.dp).background(item.second, RoundedCornerShape(3.dp)))
                                Spacer(Modifier.width(6.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(item.first, color = Ink, fontSize = 10.sp, maxLines = 1)
                                    Text(item.third, color = Muted, fontSize = 9.sp)
                                }
                            }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel), border = BorderStroke(1.dp, Color(0xFF9A8CFF).copy(alpha = .25f))) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("کاربرد عناصر در دنیای واقعی", color = Cyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                ApplicationCard("Si", "سیلیسیم", "تراشه‌ها و نیمه‌رساناها", "در بسیاری از قطعات الکترونیکی و سلول‌های خورشیدی استفاده می‌شود.", Color(0xFF79F2B3))
                ApplicationCard("Fe", "آهن", "فولاد و سازه‌ها", "عنصری مهم در تولید فولاد و آلیاژهای مهندسی.", Color(0xFFFFC96B))
                ApplicationCard("Li", "لیتیم", "باتری‌های قابل شارژ", "در مواد کاتدی و الکترولیتی برخی باتری‌ها کاربرد دارد.", Color(0xFF9A8CFF))
                ApplicationCard("O", "اکسیژن", "زیست و صنعت", "برای تنفس هوازی و بسیاری از فرایندهای اکسایش ضروری است.", Color(0xFF55E6EF))
                ApplicationCard("Cu", "مس", "سیم‌کشی و انتقال گرما", "رسانایی الکتریکی و گرمایی بالایی دارد.", Color(0xFFFF8D70))
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("نمودار شماتیک انرژی", color = Cyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("نمونهٔ تصویری برای مقایسهٔ مفهومی؛ دادهٔ آزمایشگاهی نیست.", color = Muted, fontSize = 11.sp)
                Canvas(Modifier.fillMaxWidth().height(150.dp).padding(top = 10.dp)) {
                    val values = listOf(.28f, .34f, .39f, .48f, .52f, .62f, .69f, .76f, .83f)
                    val gap = size.width / (values.size * 1.7f)
                    values.forEachIndexed { i, v ->
                        val h = size.height * v
                        val x = gap * (i * 1.7f + .35f)
                        drawRoundRect(Brush.verticalGradient(listOf(Cyan, Color(0xFF6658C9))), topLeft = Offset(x, size.height - h), size = androidx.compose.ui.geometry.Size(gap, h), cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f))
                    }
                    drawLine(Muted.copy(alpha = .45f), Offset(0f, size.height), Offset(size.width, size.height), 1f)
                }
            }
        }
    }
}

@Composable
private fun TrendLine(title: String, subtitle: String, arrow: String, color: Color) {
    Row(Modifier.fillMaxWidth().background(Navy, RoundedCornerShape(10.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(35.dp).background(color.copy(alpha = .15f), RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
            Text(arrow, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ApplicationCard(symbol: String, name: String, headline: String, detail: String, color: Color) {
    Row(Modifier.fillMaxWidth().background(Navy, RoundedCornerShape(12.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(48.dp).background(Brush.radialGradient(listOf(Color.White.copy(alpha = .25f), color.copy(alpha = .35f))), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Text(symbol, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("$name · $headline", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(detail, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun DashboardHero() {
    val transition = rememberInfiniteTransition(label = "chemistry-motion")
    val orbitAngle by transition.animateFloat(0f, 360f, infiniteRepeatable(tween(9000), RepeatMode.Restart), label = "orbit")
    val pulse by transition.animateFloat(0.82f, 1.08f, infiniteRepeatable(tween(1400), RepeatMode.Reverse), label = "pulse")
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Cyan.copy(alpha = 0.28f))) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("MANDAL · ماده در حرکت", color = Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("جهان عناصر را کشف کن", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("جدول تناوبی • پیوندها • ساختار ماده", color = Muted, fontSize = 11.sp)
                }
                Box(Modifier.size(94.dp), contentAlignment = Alignment.Center) {
                    listOf(Cyan, Color(0xFF8C7BFF), Color(0xFFFFC96B)).forEachIndexed { i, c ->
                        Box(Modifier.size((42 + i * 22).dp, (22 + i * 16).dp), contentAlignment = Alignment.Center) {
                            Surface(Modifier.fillMaxSize(), color = Color.Transparent, shape = RoundedCornerShape(50), border = BorderStroke(1.dp, c.copy(alpha = 0.75f))) {}
                        }
                    }
                    Box(Modifier.size((22 * pulse).dp).background(Brush.radialGradient(listOf(Color.White, Cyan, Color(0xFF2479B8))), RoundedCornerShape(50)))
                    Text("e⁻", Modifier.offset(x = (30 * kotlin.math.cos(Math.toRadians(orbitAngle.toDouble()))).dp, y = (30 * kotlin.math.sin(Math.toRadians(orbitAngle.toDouble()))).dp), color = Color(0xFFFFD479), fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Cyan, Color(0xFF8C7BFF), Color.Transparent))))
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf(Triple("H₂O", "آب", Color(0xFF55E6EF)), Triple("CO₂", "دی‌اکسید کربن", Color(0xFF8D9DFF)), Triple("NaCl", "نمک", Color(0xFFFFC96B))).forEach { item ->
                    Column(Modifier.weight(1f).background(Navy, RoundedCornerShape(12.dp)).padding(vertical = 9.dp, horizontal = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(item.first, color = item.third, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                        Text(item.second, color = Muted, fontSize = 9.sp, textAlign = TextAlign.Center, maxLines = 1)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            repeat(3) { n -> Box(Modifier.padding(horizontal = 2.dp).size((5 + n % 2 * 2).dp).background(item.third.copy(alpha = 0.85f), RoundedCornerShape(50))) }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("نمایش‌های متحرک شماتیک برای یادگیری؛ نه شبیه‌سازی کوانتومی دقیق.", color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun Metric(value: String, caption: String) {
    Column(Modifier.background(Navy, RoundedCornerShape(11.dp)).padding(horizontal = 11.dp, vertical = 8.dp)) {
        Text(value, color = Cyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(caption, color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun ElementTile(e: Element, selected: Boolean, onClick: () -> Unit) {
    val tint = when (e.category) {
        "alkali" -> Color(0xFF9D3348); "alkaline" -> Color(0xFFB56A28); "transition" -> Color(0xFF155DB5)
        "post" -> Color(0xFF168A83); "metalloid" -> Color(0xFF479A54); "nonmetal" -> Color(0xFF248C78)
        "halogen" -> Color(0xFF8753C5); "noble" -> Color(0xFF6946BC); "lanth" -> Color(0xFFB24D9C)
        "act" -> Color(0xFF80475E); else -> Panel
    }
    Column(Modifier.aspectRatio(0.83f).background(if (selected) Cyan.copy(alpha = 0.22f) else tint, RoundedCornerShape(9.dp))
        .clickable(onClick = onClick).padding(5.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Text(e.number.toString(), fontSize = 9.sp, color = Ink.copy(alpha = 0.8f))
        Text(e.symbol, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
        Text(e.persianName, fontSize = 8.sp, maxLines = 1)
        Text(String.format(Locale.US, "%.2f", e.mass), fontSize = 8.sp, color = Ink.copy(alpha = 0.7f))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun NativeAtom(e: Element) {
    Box(Modifier.size(105.dp), contentAlignment = Alignment.Center) {
        listOf(Cyan, Color(0xFF9A8CFF), Color(0xFFFFCE70)).forEachIndexed { index, color ->
            Box(Modifier.size((42 + index * 24).dp, (24 + index * 18).dp), contentAlignment = Alignment.Center) {
                Surface(Modifier.fillMaxSize(), color = Color.Transparent, shape = RoundedCornerShape(50), border = BorderStroke(1.dp, color.copy(alpha = 0.7f))) {}
            }
        }
        Box(Modifier.size(29.dp).background(Cyan, RoundedCornerShape(50)), contentAlignment = Alignment.Center) {
            Text(e.symbol.take(1), color = Navy, fontWeight = FontWeight.Black)
        }
    }
}

private data class Compound(val formula: String, val name: String, val numbers: List<Int>, val description: String)
private val knownCompounds = listOf(
    Compound("H₂O", "آب", listOf(1, 8, 8), "ترکیب مولکولی قطبی؛ حلال مهم در سامانه‌های زیستی و شیمیایی."),
    Compound("CO₂", "دی‌اکسید کربن", listOf(6, 8, 8), "گازی مهم در چرخهٔ کربن و فتوسنتز."),
    Compound("NaCl", "سدیم کلرید", listOf(11, 17), "ترکیب یونی شناخته‌شده که در نمک خوراکی وجود دارد."),
    Compound("NH₃", "آمونیاک", listOf(7, 1, 1, 1), "ترکیبی از نیتروژن و هیدروژن با کاربردهای صنعتی."),
    Compound("CH₄", "متان", listOf(6, 1, 1, 1, 1), "ساده‌ترین هیدروکربن و یکی از اجزای گاز طبیعی."),
    Compound("CaCO₃", "کلسیم کربنات", listOf(20, 6, 8, 8, 8), "در سنگ آهک، گچ و پوستهٔ برخی جانداران یافت می‌شود."),
    Compound("SiO₂", "سیلیسیم دی‌اکسید", listOf(14, 8, 8), "یکی از ترکیب‌های اصلی ماسه و کوارتز."),
    Compound("Fe₂O₃", "آهن(III) اکسید", listOf(26, 26, 8, 8, 8), "یکی از اکسیدهای آهن."),
    Compound("CuO", "مس(II) اکسید", listOf(29, 8), "اکسید مس؛ ترکیب شناخته‌شده در شیمی مواد.")
)

@Composable
private fun ElementPicker(label: String, number: Int, onPick: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val e = periodicElements.find { it.number == number }
    Column {
        Text(label, color = Muted, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.weight(1f)) {
                Text(e?.let { it.symbol + " — " + it.persianName } ?: "انتخاب عنصر")
            }
            if (label.contains("سوم") && number != 0) TextButton(onClick = { onPick(0) }) { Text("پاک‌کردن") }
        }
        if (expanded) {
            LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(130.dp).background(Navy, RoundedCornerShape(9.dp))) {
                items(periodicElements, key = { it.number }) { item ->
                    Text(item.symbol + "\n" + item.persianName, textAlign = TextAlign.Center,
                        color = if (item.number == number) Cyan else Ink,
                        modifier = Modifier.clickable { onPick(item.number); expanded = false }.padding(5.dp), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun NativeMolecule(elements: List<Element>) {
    var dragRotation by remember { mutableFloatStateOf(0f) }
    val transition = rememberInfiniteTransition(label = "molecule-3d")
    val angle by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(11000), RepeatMode.Restart),
        label = "molecule-rotation"
    )
    val palette = listOf(Color(0xFF55E6EF), Color(0xFFFFC96B), Color(0xFF9A8CFF), Color(0xFF79F2B3), Color(0xFFFF719D))
    Column(Modifier.fillMaxWidth().background(Navy, RoundedCornerShape(16.dp)).padding(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("MOLECULAR VIEW / 3D", color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text("نمایش چرخان شماتیک", color = Muted, fontSize = 10.sp)
        }
        Canvas(Modifier.fillMaxWidth().height(150.dp).pointerInput(Unit) { detectDragGestures { change, dragAmount -> change.consume(); dragRotation += dragAmount.x * 0.7f } }) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val count = elements.size.coerceAtMost(8)
            if (count == 0) {
                drawCircle(Cyan.copy(alpha = .12f), radius = 38f, center = Offset(cx, cy))
                drawCircle(Cyan, radius = 5f, center = Offset(cx, cy))
            } else {
                val coords = (0 until count).map { i ->
                    val theta = (2.0 * Math.PI * i / count) + Math.toRadians((angle + dragRotation).toDouble())
                    val ring = if (count <= 2) 44.0 else if (i % 2 == 0) 55.0 else 38.0
                    val x3 = kotlin.math.cos(theta) * ring
                    val y3 = kotlin.math.sin(theta) * ring * .64
                    val z3 = kotlin.math.sin(theta + 1.1) * 30.0
                    val perspective = 1.0 + z3 / 180.0
                    Offset(cx + (x3 * perspective).toFloat(), cy + (y3 * perspective).toFloat())
                }
                for (i in 0 until count) {
                    val next = if (count == 2) 1 - i else (i + 1) % count
                    if (i < next || count == 2) {
                        drawLine(Color(0xFF75E9F4).copy(alpha = .65f), coords[i], coords[next], strokeWidth = 3f)
                        drawLine(Color.White.copy(alpha = .18f), coords[i] + Offset(0f, 2f), coords[next] + Offset(0f, 2f), strokeWidth = 1f)
                    }
                }
                val order = (0 until count).sortedBy { coords[it].y }
                order.forEach { i ->
                    val e = elements[i]
                    val tint = palette[e.number % palette.size]
                    val radius = (if (count > 4) 15f else 20f) + ((coords[i].y - cy) / size.height * 5f)
                    drawCircle(Brush.radialGradient(listOf(Color.White, tint, tint.copy(alpha = .55f)), center = coords[i] + Offset(-radius * .28f, -radius * .32f), radius = radius * 1.8f), radius = radius, center = coords[i])
                    drawCircle(Color.White.copy(alpha = .7f), radius = 1.5f, center = coords[i] + Offset(-radius * .28f, -radius * .32f))
                }
            }
            // Subtle HUD rings give the molecular viewport a sci-fi instrument-panel feel.
            drawCircle(Cyan.copy(alpha = .12f), radius = 61f, center = Offset(cx, cy), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
            drawCircle(Color(0xFF9A8CFF).copy(alpha = .13f), radius = 74f, center = Offset(cx, cy), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
        }
        if (elements.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.Center) {
                elements.take(8).forEachIndexed { i, e ->
                    Text(e.symbol, color = palette[e.number % palette.size], fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                }
            }
        }
    }
}
