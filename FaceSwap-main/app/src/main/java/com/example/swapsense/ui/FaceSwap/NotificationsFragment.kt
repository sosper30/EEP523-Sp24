package com.example.swapsense.ui.FaceSwap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.swapsense.databinding.FragmentNotificationsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Constants and state variables.
    private val tag = "MainActivity"
    private val face1Tab = 0
    private val face2Tab = 1
    private val pickImage = 100
    private var selectedTab = 0

    private val desiredWidth = 800
    private val desiredHeight = 800

    // Variables to hold image URIs for faces.
    private var imageUriFace1: Uri? = null
    private var imageUriFace2: Uri? = null

    // Bitmaps for original and swapped faces.
    private lateinit var bitmap1: Bitmap
    private lateinit var bitmap2: Bitmap
    private lateinit var bitmap1Swapped: Bitmap
    private lateinit var bitmap2Swapped: Bitmap

    // UI components.
    private lateinit var imageView: ImageView
    private lateinit var swapButton: FloatingActionButton

    private lateinit var faces1: List<Face>
    private lateinit var faces2: List<Face>
    // Engine for face detection.
    private val faceDetectorEngine = FaceDetectorEngine()

    // Flags to track the completion of face detection and swapping.
    private var face1Done = false
    private var face2Done = false
    private var okToSwap = false
    private var hasSwapped = false

    private lateinit var addImageButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabs = binding.tabLayout
        swapButton = binding.fab
        swapButton.isEnabled = false
        imageView = binding.imageView
        addImageButton = binding.buttonAddImage
        // Change tabs

        // Setup listener for tab selection to switch images and visibility of add button.
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab != null) {
                    Log.d(tag, "Tab ${tab.position} selected")

                    selectedTab = tab.position

                    if (hasSwapped) {
                        // Swapped, used swapped bitmaps instead of source.
                        if (tab.position == face1Tab) {
                            imageView.setImageBitmap(bitmap1Swapped)
                        }
                        if (tab.position == face2Tab) {
                            imageView.setImageBitmap(bitmap2Swapped)
                        }
                    } else {
                        // Has not swapped, use sources.
                        if (tab.position == face1Tab) {
                            if (imageUriFace1 != null) {
                                addImageButton.visibility = Button.GONE
                            } else {
                                addImageButton.visibility = Button.VISIBLE
                            }
                            imageView.setImageURI(imageUriFace1)

                        }

                        if (tab.position == face2Tab) {
                            if (imageUriFace2 != null) {
                                addImageButton.visibility = Button.GONE

                            } else {
                                addImageButton.visibility = Button.VISIBLE
                            }
                            imageView.setImageURI(imageUriFace2)
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d(tag, "onTabReselected not in use.")
                if (tab != null) {
                    if (tab.position == face1Tab) {
                        if (imageUriFace1 != null) {
                            addImageButton.visibility = Button.GONE
                        } else {
                            addImageButton.visibility = Button.VISIBLE
                        }
                    }

                    if (tab.position == face2Tab) {
                        if (imageUriFace2 != null) {
                            addImageButton.visibility = Button.GONE
                        } else {
                            addImageButton.visibility = Button.VISIBLE
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d(tag, "onTabUnselected not in use.")
            }
        })

        // Open gallery for image selection

        addImageButton.setOnClickListener {
            Log.d(tag, "Click on image view.")
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // Request gallery permission if not granted
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_CODE)
//                requestStoragePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            } else {
                // Open the gallery if permission is granted
                openGallery()
            }

        }

        // Click listener for action button, should result in face swap.
        swapButton.setOnClickListener {
            Log.d(tag, "Action button clicked.")

            if (okToSwap) {
                Log.d(tag, "Ready to swap!")

                val landmarksForFaces1 = Landmarks.arrangeLandmarksForFaces(faces1)
                val landmarksForFaces2 = Landmarks.arrangeLandmarksForFaces(faces2)

                bitmap2Swapped =
                    Swap.faceSwapAll(bitmap1, bitmap2, landmarksForFaces1, landmarksForFaces2)
                bitmap1Swapped =
                    Swap.faceSwapAll(bitmap2, bitmap1, landmarksForFaces2, landmarksForFaces1)


                if (selectedTab == face1Tab) {
                    imageView.setImageBitmap(bitmap1Swapped)
                }

                if (selectedTab == face2Tab) {
                    imageView.setImageBitmap(bitmap2Swapped)
                }

                hasSwapped = true

            }
        }

    }

    // Gallery
    // Function to open Gallery
    private fun openGallery() {
//        val gallery =
//            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"  // Set the type to image
        val pickerIntent = Intent.createChooser(intent, "Select Picture")
        if (pickerIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, IMAGE_GALLERY_CODE)

        } else {
            Log.d("Gallery", "No Intent available to handle action")
        }
    }

    // Callback for the result from requesting permissions
    // Function to handle image selection and permission requests.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("Debug","in onRequestPermissionsResult")
        Log.d("requestCode","${requestCode}")

        if (requestCode == READ_EXTERNAL_STORAGE_CODE ){
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) ){
                // Permission was granted, open the camera
                openGallery()
            } else {
                // Permission was denied, handle the case
                Log.d("permission","Permission was denied")
            }
        }

    }

    // Handle the result from gallery selection.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(tag, "onActivityResult: Image selected.")

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == IMAGE_GALLERY_CODE) {

            swapButton.isEnabled = false

            if (selectedTab == face1Tab) {
                imageUriFace1 = data?.data
                imageView.setImageURI(imageUriFace1)
                imageUriFace1?.let { prepareImage(it, 0) }

                addImageButton.visibility = Button.GONE

            }
            if (selectedTab == face2Tab) {
                imageUriFace2 = data?.data
                imageView.setImageURI(imageUriFace2)
                imageUriFace2?.let { prepareImage(it, 1)
                }
                addImageButton.visibility = Button.GONE
            }

        }
    }


    // Prepares chosen image for face detection
    private fun prepareImage(uri: Uri, faceIndex: Int) {
        Log.d(tag, "prepareImage: Preparing image for face detection.")

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>(desiredWidth, desiredHeight) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    val inputImage = InputImage.fromBitmap(resource, 0)
                    hasSwapped = false

                    when (faceIndex) {
                        0 -> bitmap1 = resource
                        else -> bitmap2 = resource
                    }

                    faceDetectorEngine.detectInImage(inputImage)
                        .addOnSuccessListener { faces ->
                            when (faceIndex) {
                                0 -> faces1 = faces
                                else -> faces2 = faces
                            }

                            val notEmpty = faces.isNotEmpty()
                            if (notEmpty && faceIndex == 0) {
                                face1Done = true
                            }
                            if (notEmpty && faceIndex == 1) {
                                face2Done = true
                            }

                            okToSwap = face1Done && face2Done
                            swapButton.isEnabled = okToSwap
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })


    }

    //    Draws landmarks for a face. Only for debugging.
    private fun drawLandmarks(uri: Uri, landmarksForFaces: ArrayList<ArrayList<PointF>>) {
        Log.v(tag, "Draw landmarks for faces.")

        Glide.with(this)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>(desiredWidth, desiredHeight) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val bitmapWithLandmarks =
                        ImageUtils.drawLandmarksOnBitmap(resource, landmarksForFaces)

                    imageView.setImageBitmap(bitmapWithLandmarks)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    companion object {
        // Request codes for camera and permissions
        private const val IMAGE_GALLERY_CODE = 1005
        private const val READ_EXTERNAL_STORAGE_CODE = 1004
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}