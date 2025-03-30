package ru.nikiforova.drivenext.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.nikiforova.drivenext.R
import ru.nikiforova.drivenext.data.Car

class CarAdapter(private var carList: List<Car>) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val carName: TextView = itemView.findViewById(R.id.car_name)
        private val carBrand: TextView = itemView.findViewById(R.id.car_brand)
        private val carPrice: TextView = itemView.findViewById(R.id.car_price)
        private val transmission: TextView = itemView.findViewById(R.id.transmission)
        private val engineType: TextView = itemView.findViewById(R.id.engine_type)
        private val carImage: ImageView = itemView.findViewById(R.id.car_image)

        fun bind(car: Car) {
            carName.text = car.name
            carBrand.text = car.brand
            carPrice.text = itemView.context.getString(R.string.per_day, car.price)
            transmission.text = car.transmission
            engineType.text = car.engineType
            carImage.setImageResource(car.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.car_item, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(carList[position])
    }

    override fun getItemCount(): Int = carList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Car>) {
        carList = newList
        notifyDataSetChanged()
    }
}