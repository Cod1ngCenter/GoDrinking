package com.godrinking.app.data

import java.util.Calendar
import java.util.Date

// ── Enums ─────────────────────────────────────────────────────────────────────

enum class UserRole(val label: String) {
    GERENTE("Gerente"),
    DEV("Dev")
}

enum class ClientStatus(val label: String) {
    CONVERTIDO("Convertido"),
    EM_ANDAMENTO("Em andamento"),
    PERDIDO("Perdido")
}

enum class PackageType(val label: String, val pricePerPerson: Int) {
    ORLOFF("Pacote Orloff", 35),
    SMIRNOFF("Pacote Smirnoff", 55),
    ABSOLUT("Pacote Absolut", 85)
}

enum class StockCategory(val label: String) {
    BEBIDAS("Bebidas"),
    INSUMOS("Insumos"),
    EQUIPAMENTOS("Equipamentos")
}

enum class StockStatus(val label: String) {
    CRITICAL("Crítico"),
    WARNING("Atenção"),
    NORMAL("Normal")
}

// ── Data Classes ──────────────────────────────────────────────────────────────

data class Address(
    val street: String       = "",
    val number: String       = "",
    val neighborhood: String = "",
    val city: String         = "",
    val state: String        = "",
    val zip: String          = ""
)

data class Client(
    val id: Int,
    val name: String,
    val document: String,
    val phone: String,
    val email: String,
    val status: ClientStatus,
    val registeredAt: String,
    val address: Address? = null
)

data class Event(
    val id: Int,
    val name: String,
    val client: String,
    val type: String,
    val location: String,
    val date: Date,
    val time: String,
    val people: Int,
    val packageType: PackageType,
    val services: List<String>,
    val observations: String,
    val finalValue: Double,
    val status: String = "Confirmado"
)

data class Quotation(
    val id: Int,
    val name: String,
    val client: String,
    val date: Date,
    val people: Int,
    val packageType: PackageType,
    val value: Double,
    val status: String // "Pendente", "Aprovado", "Expirado"
)

data class StockItem(
    val id: Int,
    val name: String,
    val category: StockCategory,
    val quantity: Int,
    val threshold: Int,
    val estimatedValue: Double
) {
    val status: StockStatus
        get() = when {
            quantity <= threshold * 0.5 -> StockStatus.CRITICAL
            quantity <= threshold       -> StockStatus.WARNING
            else                        -> StockStatus.NORMAL
        }
}

data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)

data class Notification(
    val id: Int,
    val type: String,
    val title: String,
    val message: String,
    val time: String,
    val read: Boolean
)

// ── Sample / Mock Data ────────────────────────────────────────────────────────

object SampleData {

    val clients = listOf(
        Client(
            1, "Maria Silva", "123.456.789-00",
            "(11) 98765-4321", "maria@email.com",
            ClientStatus.CONVERTIDO, "15/04/2026",
            Address("Rua das Flores", "123", "Centro", "São Paulo", "SP", "01234-567")
        ),
        Client(2, "João Santos", "987.654.321-00", "(11) 91234-5678",
            "joao@email.com", ClientStatus.EM_ANDAMENTO, "20/04/2026"),
        Client(3, "Empresa XYZ LTDA", "12.345.678/0001-90", "(11) 3456-7890",
            "contato@xyz.com", ClientStatus.CONVERTIDO, "22/04/2026"),
        Client(4, "Ana Costa", "456.789.123-00", "(11) 99876-5432",
            "ana@email.com", ClientStatus.PERDIDO, "18/04/2026"),
        Client(5, "Pedro Lima", "321.654.987-00", "(11) 97777-8888",
            "pedro@email.com", ClientStatus.EM_ANDAMENTO, "25/04/2026"),
    )

    private fun date(year: Int, month: Int, day: Int): Date =
        Calendar.getInstance().apply { set(year, month, day, 0, 0, 0) }.time

    val events = listOf(
        Event(1, "Casamento Maria & João", "Maria Silva", "Casamento",
            "Espaço Império", date(2026, 4, 5), "20:00", 200,
            PackageType.ABSOLUT, listOf("Bartender Premium", "Decoração de Balcão"),
            "Noivos solicitam drinks personalizados temáticos.", 8500.0),
        Event(2, "Formatura Medicina UNESP", "João Santos", "Formatura",
            "Hotel Plaza", date(2026, 4, 12), "21:00", 150,
            PackageType.SMIRNOFF, listOf("Bartender Padrão"), "", 5200.0),
        Event(3, "Evento Corporativo XYZ", "Empresa XYZ", "Corporativo",
            "Centro de Convenções", date(2026, 4, 18), "18:00", 300,
            PackageType.ORLOFF, emptyList(),
            "Evento institucional, bebidas sem álcool disponíveis.", 12000.0),
        Event(4, "Aniversário 30 anos Ana", "Ana Costa", "Aniversário",
            "Casa de Festas Ipê", date(2026, 5, 8), "19:00", 80,
            PackageType.SMIRNOFF, listOf("Anão Coqueteleiro"), "", 3200.0),
        Event(5, "Formatura Direito USP", "Pedro Lima", "Formatura",
            "Club Pinheiros", date(2026, 6, 22), "21:30", 120,
            PackageType.ABSOLUT, listOf("Bartender Premium", "DJ"),
            "Evento ao ar livre – verificar cobertura.", 7800.0),
    )

    val stockItems = listOf(
        StockItem(1, "Vodka Orloff",         StockCategory.BEBIDAS,       45,  20, 340.0),
        StockItem(2, "Vodka Smirnoff",       StockCategory.BEBIDAS,       12,  20, 480.0),
        StockItem(3, "Vodka Absolut",        StockCategory.BEBIDAS,        8,  15, 600.0),
        StockItem(4, "Copos Descartáveis",   StockCategory.INSUMOS,        3,  10, 150.0),
        StockItem(5, "Taças de Vidro",       StockCategory.EQUIPAMENTOS, 150,  50, 3000.0),
        StockItem(6, "Balcão Portátil",      StockCategory.EQUIPAMENTOS,   5,   2, 2500.0),
        StockItem(7, "Gelo (pacotes)",       StockCategory.INSUMOS,        2,   5,  80.0),
        StockItem(8, "Limão",               StockCategory.INSUMOS,        30,  20,  45.0),
    )

    val services = listOf(
        Service(1, "Bartender Padrão",        "Bartender para eventos de até 200 pessoas",    350.0, "Pessoal"),
        Service(2, "Bartender Premium",       "Bartender sênior, especialista em drinks",      550.0, "Pessoal"),
        Service(3, "Anão Coqueteleiro",       "Atração especial – apresentação coqueteleira", 800.0, "Entretenimento"),
        Service(4, "DJ",                      "DJ para eventos com equipamento próprio",      1200.0, "Entretenimento"),
        Service(5, "Decoração de Balcão",     "Decoração temática para o bar do evento",       400.0, "Decoração"),
        Service(6, "Transporte de Equipamentos","Frete para transporte de todo o equipamento", 250.0, "Logística"),
        Service(7, "Assessoria de Bebidas",   "Consultoria para escolha do cardápio de drinks",300.0, "Consultoria"),
    )

    val notifications = listOf(
        Notification(1, "stock",  "Estoque Crítico",      "Copos Descartáveis com apenas 3 unidades",              "5 min atrás",  false),
        Notification(2, "event",  "Evento Próximo",       "Casamento Maria Silva em 3 dias",                        "2 horas atrás",false),
        Notification(3, "budget", "Orçamento Pendente",   "Proposta para João Santos aguardando resposta",          "1 dia atrás",  true),
        Notification(4, "client", "Novo Cliente",         "Ana Costa foi cadastrada no sistema",                    "2 dias atrás", true),
        Notification(5, "stock",  "Estoque em Atenção",   "Vodka Smirnoff com apenas 12 unidades",                  "2 dias atrás", true),
    )

    val quotations = listOf(
        Quotation(1, "Orçamento Casamento Maria", "Maria Silva", date(2026, 4, 5), 200, PackageType.ABSOLUT, 8500.0, "Aprovado"),
        Quotation(2, "Aniversário 15 anos Júlia", "Carlos Souza", date(2026, 5, 20), 120, PackageType.SMIRNOFF, 4200.0, "Pendente"),
        Quotation(3, "Evento Corporativo Tech", "Tech Solutions", date(2026, 4, 25), 300, PackageType.ABSOLUT, 12500.0, "Pendente"),
        Quotation(4, "Churrasco Amigos", "Felipe Rocha", date(2026, 3, 10), 50, PackageType.ORLOFF, 1800.0, "Expirado"),
    )

    val extraServices = listOf(
        Pair("balcao",           Pair("Aluguel de Balcão Portátil",       500)),
        Pair("copos_extra",      Pair("Copos/Taças Extras (kit 50)",      150)),
        Pair("bartender_extra",  Pair("Bartender Adicional (+2h)",        300)),
        Pair("anao",             Pair("Anão Coqueteleiro",                800)),
        Pair("dj",               Pair("DJ (serviço parceiro)",           1200)),
        Pair("transporte",       Pair("Frete / Transporte",               250)),
    )
}
