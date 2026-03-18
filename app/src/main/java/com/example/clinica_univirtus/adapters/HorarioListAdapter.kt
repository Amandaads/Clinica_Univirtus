package com.example.clinica_univirtus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.clinica_univirtus.R

class HorarioListAdapter(private val listaHorarios: List<String>, private val onClick: (String) -> Unit) : RecyclerView.Adapter<HorarioListAdapter.HorarioViewHolder>() {

    private var posicaoSelecionada = -1
    class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val horario: Button = itemView.findViewById(R.id.btn_horario)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_horario, parent, false)
        return HorarioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HorarioViewHolder,
        position: Int
    ) {
        val horario = listaHorarios[position]
        holder.horario.text = horario

        if (position == posicaoSelecionada) {
            holder.horario.setBackgroundColor("#D6ECFF".toColorInt())
            holder.horario.elevation = 8f
        } else {
            holder.horario.setBackgroundColor("#FFFFFF".toColorInt())
            holder.horario.elevation = 0f
        }

        holder.horario.setOnClickListener {
            val position = holder.bindingAdapterPosition

            if (position != RecyclerView.NO_POSITION) {
                val posicaoAnterior = posicaoSelecionada
                posicaoSelecionada = position

                notifyItemChanged(posicaoAnterior)
                notifyItemChanged(posicaoSelecionada)

                onClick(horario)
            }
        }
    }

    override fun getItemCount(): Int = listaHorarios.size


}