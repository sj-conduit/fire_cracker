package com.afkanerd.deku.QueueListener.GatewayClients

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.afkanerd.deku.DefaultSMS.Models.SIMHandler
import com.afkanerd.deku.DefaultSMS.R
import com.afkanerd.deku.Modules.ThreadingPoolExecutor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class GatewayClientProjectAddModalFragment(private val gatewayClientProjectListingViewModel:
                                           GatewayClientProjectListingViewModel,
                                           private val gatewayClientId: Long,
                                           private var gatewayClientProjects:
                                           GatewayClientProjects? = GatewayClientProjects()) :
    BottomSheetDialogFragment(R.layout.fragment_modalsheet_gateway_client_project_add_edit) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getGatewayClient(view)

        val materialButton = view.findViewById<MaterialButton>(R.id.gateway_client_customization_save_btn)
        materialButton.setOnClickListener { v ->
            try {
                onSaveGatewayClientConfiguration(view)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        val toolbar = view.findViewById<Toolbar>(R.id.gateway_client_project_add_edit_toolbar)
        toolbar.title = view.context.getString(R.string.gateway_client_add_edit_add_gateway_client)

        val bottomSheet = view.findViewById<View>(R.id.gateway_client_project_add_edit_layout)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getGatewayClient(view: View) {
        val projectName = view.findViewById<TextInputEditText>(R.id.new_gateway_client_project_name)
        val projectBinding =
            view.findViewById<TextInputEditText>(R.id.new_gateway_client_project_binding_sim_1)
        val projectBinding2 =
            view.findViewById<TextInputEditText>(R.id.new_gateway_client_project_binding_sim_2)

        val isDualSim = SIMHandler.isDualSim(view.context)
        if (isDualSim) {
            view.findViewById<View>(R.id.new_gateway_client_project_binding_sim_2_layout)
                .visibility = View.VISIBLE
        }

        gatewayClientProjects?.let {
            activity?.runOnUiThread {
                projectName.setText(gatewayClientProjects!!.name)
                projectBinding.setText(gatewayClientProjects!!.binding1Name)
                if (isDualSim) {
                    projectBinding2.setText(gatewayClientProjects!!.binding2Name)
                }
            }
        }

        projectName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val projectBindings = GatewayClientHandler
                    .getPublisherDetails( view.context, s.toString())

                projectBinding.setText(projectBindings[0])
                if (projectBindings.size > 1) {
                    projectBinding2.setText(projectBindings[1])
                }
            }
        })
    }

    private fun onSaveGatewayClientConfiguration(view: View) {
        val projectName = view.findViewById<TextInputEditText>(R.id.new_gateway_client_project_name)
        val projectBinding =
            view.findViewById<TextInputEditText>(R.id.new_gateway_client_project_binding_sim_1)
        val projectBinding2 =
            view.findViewById<TextInputEditText>(R.id.new_gateway_client_project_binding_sim_2)
        val projectBindingConstraint =
            view.findViewById<LinearLayout>(R.id.new_gateway_client_project_binding_sim_2_layout)

        if (projectName.text == null || projectName.text.toString().isEmpty()) {
            projectName.error = getString(R.string.settings_gateway_client_cannot_be_empty)
            return
        }

        if (projectBinding.text == null || projectBinding.text.toString().isEmpty()) {
            projectBinding.error = getString(R.string.settings_gateway_client_cannot_be_empty)
            return
        }

        if (projectBindingConstraint.visibility == View.VISIBLE &&
            (projectBinding2.text == null || projectBinding2.text.toString().isEmpty())) {
            projectBinding2.error = getString(R.string.settings_gateway_client_cannot_be_empty)
            return
        }

        if(gatewayClientProjects == null)
            gatewayClientProjects = GatewayClientProjects()

        gatewayClientProjects?.name = projectName.text.toString()
        gatewayClientProjects?.binding1Name = projectBinding.text.toString()
        gatewayClientProjects?.binding2Name = projectBinding2.text.toString()
        gatewayClientProjects?.gatewayClientId = gatewayClientId

        ThreadingPoolExecutor.executorService.execute {
            try {
                gatewayClientProjectListingViewModel.insert(gatewayClientProjects!!)
            } catch(e: Exception) {
                e.printStackTrace()
            }
            dismiss()
        }
    }

    companion object {
        const val GATEWAY_CLIENT_PROJECT_ID: String = "GATEWAY_CLIENT_PROJECT_ID"
    }
}