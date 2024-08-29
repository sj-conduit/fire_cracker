package com.afkanerd.deku.QueueListener.GatewayClients

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afkanerd.deku.DefaultSMS.R

class GatewayClientListingFragment : Fragment(R.layout.fragment_gateway_client_listing) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gatewayClientViewModel: GatewayClientViewModel by viewModels()
        val gatewayClientRecyclerAdapter = GatewayClientRecyclerAdapter()

        val linearLayoutManager = LinearLayoutManager(view.context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.gateway_client_listing_recycler_view)
        recyclerView.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(view.context,
                linearLayoutManager.orientation )
        recyclerView.addItemDecoration(dividerItemDecoration)

        recyclerView.adapter = gatewayClientRecyclerAdapter

        gatewayClientRecyclerAdapter.onSelectedListener.observe(viewLifecycleOwner, Observer {
            it?.let {
                gatewayClientRecyclerAdapter.onSelectedListener = MutableLiveData()

                val gatewayClientProjectListingFragment = GatewayClientProjectListingFragment(it.id)
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace( R.id.view_fragment, gatewayClientProjectListingFragment)
                        ?.setReorderingAllowed(true)
                        ?.addToBackStack(gatewayClientProjectListingFragment.javaClass.name)
                        ?.commit()
            }
        })

        gatewayClientViewModel.getGatewayClientList(view.context).observe(this,
                Observer {
                    if (it.isNullOrEmpty())
                        view.findViewById<View>(R.id.gateway_client_no_gateway_client_label)
                                .visibility = View.VISIBLE
                    gatewayClientRecyclerAdapter.submitList(it)
                })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.gateway_client_listing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.gateway_client_add_manually) {
            val addGatewayIntent = Intent(requireContext(), GatewayClientAddActivity::class.java)
            startActivity(addGatewayIntent)
            return true
        }
        return false
    }

}