<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools">

    <data class=".${bindingName}">

        <variable
            name="viewModel"
            type="${packageName}.${viewModelName}" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context="${packageName}.${className}Fragment">

    </androidx.constraintlayout.widget.ConstraintLayout>
    
</layout>