@file:OptIn(InternalResourceApi::class)

package campuscompass.shared.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceContentHash
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/campuscompass.shared.generated.resources/"

@delegate:ResourceContentHash(1_873_850_578)
internal val Res.drawable.campus_compass_banner: DrawableResource by lazy {
      DrawableResource("drawable:campus_compass_banner", setOf(
        ResourceItem(setOf(), "${MD}drawable/campus_compass_banner.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(379_089_144)
internal val Res.drawable.compose_multiplatform: DrawableResource by lazy {
      DrawableResource("drawable:compose_multiplatform", setOf(
        ResourceItem(setOf(), "${MD}drawable/compose-multiplatform.xml", -1, -1),
      ))
    }

@delegate:ResourceContentHash(2_099_674_437)
internal val Res.drawable.logo: DrawableResource by lazy {
      DrawableResource("drawable:logo", setOf(
        ResourceItem(setOf(), "${MD}drawable/logo.jpg", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("campus_compass_banner", Res.drawable.campus_compass_banner)
  map.put("compose_multiplatform", Res.drawable.compose_multiplatform)
  map.put("logo", Res.drawable.logo)
}
