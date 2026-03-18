package com.example.clinica_univirtus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clinica_univirtus.R



class HorarioListAdapter(val listaHorarios: List<String>) : RecyclerView.Adapter<HorarioListAdapter.HorarioViewHolder>() {

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
    }

    override fun getItemCount(): Int = listaHorarios.size

    class HorarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val horario: TextView = itemView.findViewById(R.id.btn_horario)
    }

}