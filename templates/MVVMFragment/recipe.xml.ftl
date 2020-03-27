<?xml version="1.0"?>
<#import "root://activities/common/kotlin_macros.ftl" as kt>
<recipe>
    
    <instantiate from="root/res/layout/mvvm_fragment.xml.ftl"
                to="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(layoutName)}.xml" />

    <open file="${escapeXmlAttribute(resOut)}/layout/${escapeXmlAttribute(layoutName)}.xml" />

    <instantiate from="root/src/app_package/MvvmFragment.kt.ftl"
                    to="${escapeXmlAttribute(srcOut)}/${className}Fragment.kt" />

    <open file="${escapeXmlAttribute(srcOut)}/${className}Fragment.kt" />

    <instantiate from="root/src/app_package/MvvmViewModel.kt.ftl"
                    to="${escapeXmlAttribute(srcOut)}/${viewModelName}.kt" />

    <open file="${escapeXmlAttribute(srcOut)}/${viewModelName}.kt" />

</recipe>