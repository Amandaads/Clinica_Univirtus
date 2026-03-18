package com.example.clinica_univirtus.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.clinica_univirtus.R
import com.example.clinica_univirtus.models.Agendamento
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AgendamentoListAdapter(
    private val lista: MutableList<Agendamento>,
    private val onCancelar: (Agendamento) -> Unit
) : RecyclerView.Adapter<AgendamentoListAdapter.AgendamentoViewHolder>() {

    class AgendamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val data: TextView = itemView.findViewById(R.id.textData)
        val hora: TextView = itemView.findViewById(R.id.textHora)
        val especialidade: TextView = itemView.findViewById(R.id.textEspecialidade)
        val medico: TextView = itemView.findViewById(R.id.textMedico)
        val cancelar: Button = itemView.findViewById(R.id.btn_cancelar)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendamentoViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agendamento, parent, false)

        return AgendamentoViewHolder(view)
    }

    override fun getItemCount() = lista.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: AgendamentoViewHolder, position: Int) {

        val agendamento = lista[position]

        holder.data.text = "Data: ${formatarData(agendamento.data)}"
        holder.hora.text = "Horário: ${agendamento.hora}"
        holder.especialidade.text = "Especialidade: ${agendamento.especialidade}"
        holder.medico.text = "Médico: ${agendamento.medico}"

        holder.cancelar.setOnClickListener {
            onCancelar(agendamento)
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatarData(data: String): String {
        val entrada = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val saida = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val localDate = LocalDate.parse(data, entrada)
        return localDate.format(saida)
    }
}