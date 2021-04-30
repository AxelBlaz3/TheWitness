package com.wielabs.thewitness.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.wielabs.thewitness.R
import com.wielabs.thewitness.databinding.FragmentSignInBinding
import com.wielabs.thewitness.ui.MainActivityViewModel
import com.wielabs.thewitness.util.SharedPreferenceUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val RC_SIGN_IN = 1

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var sharedPreferenceUtils: SharedPreferenceUtils

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            signInButton.setOnClickListener {
                signIn()
            }

            mainActivityViewModel.getProfile().observe(viewLifecycleOwner) {
                it?.let { profile ->
                    findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToHomeFragment())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.email?.let { email ->
                mainActivityViewModel.refreshProfile(email = email, name = account.displayName)
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(this.javaClass.simpleName, "signInResult:failed code=" + e.statusCode)
            Snackbar.make(binding.root, getString(R.string.sign_in_failed), Snackbar.LENGTH_SHORT).apply {
                setAction(getString(R.string.retry)) { signIn() }
            }
        }
    }
}