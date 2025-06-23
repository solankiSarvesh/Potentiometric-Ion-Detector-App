<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    tools:context=".Signup"
    android:background="@color/green"
    >

    <TextView
        android:id="@+id/signup"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="60dp"
        android:fontFamily="@font/sbold"
        android:gravity="center"
        android:padding="10dp"
        android:text="Sign Up"
        android:textSize="45sp"
        android:textStyle="bold"
        android:textColor="@color/white"/>

    <ScrollView
        android:id="@+id/scrollView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

            <LinearLayout
                android:id="@+id/linearLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="196dp"
                android:orientation="vertical"
                android:padding="10dp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintHorizontal_bias="0.0"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent">

                <com.google.android.material.textfield.TextInputLayout
                    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginLeft="15dp"
                    android:layout_marginTop="5dp"
                    android:layout_marginRight="15dp"
                    android:textColorHint="@android:color/secondary_text_light_nodisable"
                    app:boxCornerRadiusBottomEnd="20px"
                    app:boxCornerRadiusBottomStart="20px"
                    app:boxCornerRadiusTopEnd="20px"
                    app:boxCornerRadiusTopStart="20px"
                    app:boxStrokeColor="#E07C19"
                    app:boxStrokeWidth="1dp"
                    app:hintTextColor="#E07C19">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/fullname"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:drawableStart="@drawable/ic_user"
                        android:drawablePadding="16dp"
                        android:fontFamily="@font/smedium"
                        android:hint="Full Name"
                        android:inputType="textPersonName"
                        android:textColor="@color/black"
                        />

                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginLeft="15dp"
                    android:layout_marginTop="5dp"
                    android:layout_marginRight="15dp"
                    android:textColorHint="@android:color/secondary_text_light_nodisable"
                    app:boxCornerRadiusBottomEnd="20px"
                    app:boxCornerRadiusBottomStart="20px"
                    app:boxCornerRadiusTopEnd="20px"
                    app:boxCornerRadiusTopStart="20px"
                    app:boxStrokeColor="#E07C19"
                    app:boxStrokeWidth="1dp"
                    app:hintTextColor="#E07C19">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/email"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:drawableStart="@drawable/ic_email"
                        android:drawablePadding="16dp"
                        android:fontFamily="@font/smedium"
                        android:hint="Email"
                        android:inputType="textEmailAddress"
                        android:textColor="@color/black"
                        />

                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginLeft="15dp"
                    android:layout_marginTop="5dp"
                    android:layout_marginRight="15dp"
                    android:textColorHint="@android:color/secondary_text_light_nodisable"
                    app:boxCornerRadiusBottomEnd="20px"
                    app:boxCornerRadiusBottomStart="20px"
                    app:boxCornerRadiusTopEnd="20px"
                    app:boxCornerRadiusTopStart="20px"
                    app:boxStrokeColor="#E07C19"
                    app:boxStrokeWidth="1dp"
                    app:hintTextColor="#E07C19">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/phone"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:drawableStart="@drawable/ic_phone"
                        android:drawablePadding="16dp"
                        android:fontFamily="@font/smedium"
                        android:hint="Phone no"
                        android:inputType="phone"
                        android:textColor="@color/black"
                         />

                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.textfield.TextInputLayout
                    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginLeft="15dp"
                    android:layout_marginTop="5dp"
                    android:layout_marginRight="15dp"
                    android:textColorHint="@android:color/secondary_text_light_nodisable"
                    app:boxCornerRadiusBottomEnd="20px"
                    app:boxCornerRadiusBottomStart="20px"
                    app:boxCornerRadiusTopEnd="20px"
                    app:boxCornerRadiusTopStart="20px"
                    app:boxStrokeColor="#E07C19"
                    app:boxStrokeWidth="1dp"
                    app:endIconMode="password_toggle"
                    app:endIconTint="#E07C19"
                    app:hintTextColor="#E07C19"
                    app:passwordToggleDrawable="@drawable/ic_visible">

                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/pass"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:drawableStart="@drawable/ic_pass"
                        android:drawablePadding="16dp"
                        android:fontFamily="@font/smedium"
                        android:hint="Password"
                        android:inputType="textPassword"
                        android:textColor="@color/black"
                        />

                </com.google.android.material.textfield.TextInputLayout>


                <Button
                    android:id="@+id/signupbtn"
                    android:layout_width="357dp"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:layout_marginTop="15dp"
                    android:backgroundTint="@color/orange"
                    android:fontFamily="@font/smedium"
                    android:padding="10dp"
                    android:text="Sign Up"
                    android:textSize="15sp" />

                <TextView
                    android:id="@+id/gt_login"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:fontFamily="@font/smedium"
                    android:padding="10dp"
                    android:text="Already have account?"
                    android:textColor="@color/white" />

            </LinearLayout>

            <ProgressBar
                android:id="@+id/pgbar"
                style="?android:attr/progressBarStyle"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="25dp"
                android:foregroundGravity="center"
                android:indeterminateTint="@color/orange"
                android:visibility="gone"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/linearLayout" />


        </androidx.constraintlayout.widget.ConstraintLayout>
    </ScrollView>


</FrameLayout>