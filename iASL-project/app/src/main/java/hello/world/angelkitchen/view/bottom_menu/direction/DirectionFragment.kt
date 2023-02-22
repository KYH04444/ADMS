package hello.world.angelkitchen.view.bottom_menu.direction

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Looper
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import hello.world.angelkitchen.R
import hello.world.angelkitchen.base.BindingFragment
import hello.world.angelkitchen.databinding.FragmentFindDirectionBinding
import hello.world.angelkitchen.util.extension.setNaverMapRender


import com.naver.maps.map.NaverMap
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons

@AndroidEntryPoint
class DirectionFragment :
    BindingFragment<FragmentFindDirectionBinding>(R.layout.fragment_find_direction),
    OnMapReadyCallback {
    private val viewModel: DirectionFragmentViewModel by activityViewModels()
    private val viewModel2: DirectionFragmentViewModel by activityViewModels()
    private var zoomRatio: Double = 11.0
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private lateinit var currentLocationLatLng: String
    private lateinit var goalGeocode: String

    var indx = 0
    var car_idx = 0

    var minute_time = 0

    var time_idx = 0

    var path_car1 = PathOverlay()
    var path_car2 = PathOverlay()
    var path_car3 = PathOverlay()
    var path_car4 = PathOverlay()
    var path_car5 = PathOverlay()

    // 초기 세팅 배터리 100프로
    var cars = arrayOf(
        Car("A", 100.0, "${128.752938356},${35.836285935}", "0","${128.752938356},${35.836285935}", 0),
        Car("B", 100.0, "${128.752938356},${35.836285935}", "0","${128.752938356},${35.836285935}",0),
        Car("C", 100.0, "${128.752938356},${35.836285935}", "0","${128.752938356},${35.836285935}",0),
        Car("D", 100.0, "${128.752938356},${35.836285935}", "0","${128.752938356},${35.836285935}",0),
        Car("E", 100.0, "${128.752938356},${35.836285935}", "0","${128.752938356},${35.836285935}",0)
    )

    override fun initView() {
        cars[0].moving = "1"
        println("111111111111111111111111111111111111111111111111111111111111111111111")
        println("111111111111111111111111111111111111111111111111111111111111111111111")
        println("111111111111111111111111111111111111111111111111111111111111111111111")
        println("111111111111111111111111111111111111111111111111111111111111111111111")
        println("111111111111111111111111111111111111111111111111111111111111111111111")
        setNaverMapRender(R.id.container_direction_map, activity?.supportFragmentManager!!, this)
        // 3번으로 넘어간다.
        // 데이터를 activity에서 받아왔다.
        getDataFromActivity()
        // 2번으로 넘어간다.
        observeData()
    }

    private fun observeData() {
        println("2222222222222222222222222222222222222222222222222222222222222222222222")
        println("2222222222222222222222222222222222222222222222222222222222222222222222")
        println("2222222222222222222222222222222222222222222222222222222222222222222222")
        println("2222222222222222222222222222222222222222222222222222222222222222222222")
        println("2222222222222222222222222222222222222222222222222222222222222222222222")
        println("2222222222222222222222222222222222222222222222222222222222222222222222")
        viewModel.curLocation.observe(this, {
            val location =
                "${it.region?.area1?.name} ${it.region?.area2?.name} ${it.region?.area3?.name} ${it.region?.area4?.name} ${it.land?.number1}-${it.land?.number2}"
            binding.etStart.setText(location)
            // 1 3 2 4찍고 7루프를 계속해서 돌리다가 버튼을 입력하면 8, 9번이 발동된다.
            println("77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777")
            println("77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777")
            println("77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777")
            println("77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777")
            println("77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777")
            println("77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777")

            var i = 1

            if (indx == 1) {
                while(i<5) {
                    viewModel.getResultPath(
                        "uzlzuhd2pa",
                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                        goalGeocode,
                        cars[i].currentcarLocation
                    )
                    i+=1
                }
                indx = 3
            }
        })

        // currentLocationLatLng에다가 나의 현위치 또는 내가 설정할 위치 내가 클릭을해서 마커를 형성했다면 내가 설정한 위치를 대입하고
        // 그게 아니라면 디폴트는 나의 현위치로 계산한다.
        // goalGeocode 에 다가는 차의 현위치 또는 차가 도착한 후의 도착 위치
        viewModel.goalLocation.observe(this, {
            println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")

            goalGeocode = "${viewModel.goalLocation.value?.x},${viewModel.goalLocation.value?.y}"

//            cars[indx].currentcarLocation
                    viewModel.getResultPath(
                        "uzlzuhd2pa",
                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                        goalGeocode,
                        cars[0].currentcarLocation
                    )
//                    println(cars[indx].currentcarLocation)
//                    println(indx)
//                    indx += 1

                // println("${true.currentLocationLatLng}")
                println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
                println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
                println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
                println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
                println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
                println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
                println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
        })

        // 버튼을 누르게 되었을 때 발생하는 일
        viewModel.getResultPath.observe(this) {
            val path = PathOverlay()
            var routesCount = 0
            val pathContainer: MutableList<LatLng> = mutableListOf(LatLng(0.1, 0.1))


            if (it != null) {
                for (pathCode in it) {
                    for (pathCodeXY in pathCode.path) {
                        pathContainer.add(LatLng(pathCodeXY[1], pathCodeXY[0]))
                        routesCount++
                    }
                }
            }
            path.coords = pathContainer.drop(1)
            path.color = Color.YELLOW
            path.map = naverMap

            println(path.coords.size)
            println(path.coords[0])
            println("찾았따!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

            if (car_idx == 0){
                path_car1.coords = path.coords
            }

            if (car_idx == 1){
                path_car2.coords = path.coords
            }

            if (car_idx == 2){
                path_car3.coords = path.coords
            }

            if (car_idx == 3){
                path_car4.coords = path.coords
            }

            if (car_idx == 4){
                path_car5.coords = path.coords
            }

//            zoomRatio = when ((it[0].summary.distance * 0.001).toInt()) {
//                in 10..29 -> 10.0
//                in 30..59 -> 8.0
//                in 60..89 -> 6.0
//                in 90..99 -> 4.0
//                in 100..999 -> 2.0
//                else -> 11.0
//            }

            val CenterLatlng =
                LatLng(it[0].path[routesCount / 2][1], it[0].path[routesCount / 2][0])
            naverMap.moveCamera(
                CameraUpdate.scrollAndZoomTo(CenterLatlng, zoomRatio)
                    .animate(CameraAnimation.Fly, 2000)
            )

            // 지정한 위치를 신고 접수 장소로 설정하였다.
            val marker = Marker()
            marker.position = LatLng(it[0].path[0][1], it[0].path[0][0])
            marker.map = naverMap

            Marker().apply {
                position = LatLng(it[0].path[0][1], it[0].path[0][0])
                icon = MarkerIcons.RED
                captionMinZoom = 12.0
                setCaptionAligns(Align.Left)
                captionText = getString(R.string.marker_arrive)
                map = naverMap
            }

            // 남은 시간을 알려준다.
            println(it[0].summary.duration / 60000)
            println("분 소요 예상")

            cars[car_idx].movingtime = it[0].summary.duration / 60000
            car_idx += 1

            // 최종적으로 맵을 형성
            binding.btnNavigation.visibility = View.VISIBLE

            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")

            if (indx == 0){
                indx = 1
            }

        }
    }

    // 데이터를 activity에서 받아왔다.
    private fun getDataFromActivity() {
        println("33333333333333333333333333333333333333333333333333333333333333333333333")
        println("33333333333333333333333333333333333333333333333333333333333333333333333")
        println("33333333333333333333333333333333333333333333333333333333333333333333333")
        println("33333333333333333333333333333333333333333333333333333333333333333333333")
        println("33333333333333333333333333333333333333333333333333333333333333333333333")

        val bundle = arguments
        if (bundle != null) {
            binding.etArrive.setText(bundle.getString("share_address"))
        }
    }

    // This function loads an image file from the "res/drawable" folder and returns it as a Bitmap
    fun getMarkerImage(context: DirectionFragment, resourceId: Int): Bitmap {
        // Decode the image file and create a Bitmap object from it
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)

        return bitmap
    }

    // This function adds a marker to the specified NaverMap at the specified coordinates,
// using the specified image file as the marker icon
    fun addMarker(naverMap: NaverMap, latitude: Double, longitude: Double, markerImage: Bitmap) {
        // Create a Marker object and set its position and icon
        val marker = Marker()
        marker.position = LatLng(latitude, longitude)
        marker.icon = OverlayImage.fromBitmap(markerImage)

        // Add the marker to the map
        marker.map = naverMap
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(naverMap: NaverMap) {
        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
        println("444444444444444444444444444444444444444444444444444444444444444444444444444")

        this.naverMap = naverMap
        val uiSetting = naverMap.uiSettings
        //val markerImage = getMarkerImage(this, R.drawable.center)
//
        // val bitmap = BitmapFactory.decodeResource(resources, R.drawable.center)


        // 관제탑 = 영남대역
        Marker().apply {
            position = LatLng(35.836285935, 128.752938356)
            icon = MarkerIcons.BLACK
            captionMinZoom = 12.0
            setCaptionAligns(Align.Left)
            captionText = getString(R.string.marker_caption)
            map = naverMap
        }

//        icon = OverlayImage.fromResource(R.drawable.ic_home)

        // 관제탑의 이미지 파일 넣는 부분
        // 관제탑 = 영남대역
//        val markerImage = getMarkerImage(this, R.drawable.ic_remove)
//        addMarker(this.naverMap, 35.500925, 128.451277, markerImage)

        binding.btnLocation.map = naverMap

        val locationSource = FusedLocationSource(this, 1000)
        naverMap.locationSource = locationSource

        // 사용자 위치 오버레이 띄움
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        // 현재 위치로 이동
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
//            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 3000
        }

        // 좌표를 통해 주소를 반환해준다.
        // location 정보가 들어올때 마다 반복
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                super.onLocationResult(location)
                currentLocation = location.lastLocation
                val lat = currentLocation.latitude
                val lng = currentLocation.longitude
                currentLocationLatLng = "$lng,$lat"
                viewModel.getReverseGeoApi(
                    "uzlzuhd2pa",
                    "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                    currentLocationLatLng
                )
                cars[0].moveToTarget()
                cars[1].moveToTarget()
                cars[2].moveToTarget()
                cars[3].moveToTarget()
                cars[4].moveToTarget()

                var k = 0
                var min_movingtime = 100000
                var min_moving_idx = 0

                // 모두 추적이 끝났다면 어떤 차와 매칭할지 결정
                if (car_idx == 5){
                    while (k <= 60){
                        if(min_movingtime >= cars[k].movingtime){
                            min_movingtime = cars[k].movingtime
                            min_moving_idx = k
                        }
                        k+=1
                    }
//                    car_idx = 0
                }

                if (car_idx == 4){
                    while (k <= 60){
                        if(min_movingtime >= cars[k].movingtime){
                            min_movingtime = cars[k].movingtime
                            min_moving_idx = k
                        }
                        k+=1
                    }
//                    car_idx = 0
                }

                // moving time 초기화 시켜주어야...
                // 그 전에 1분 단위마다 업데이트한다 차의 위치를
                var current_state = 0

                println(car_idx)
                println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")

                time_idx += 1
                if ((time_idx >= 3) && (car_idx == 5)){
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    minute_time += 1
                    time_idx = 0
                    current_state = path_car1.coords.size - minute_time * (path_car1.coords.size / min_movingtime)

                    Marker().apply {
                        position = path_car1.coords[current_state]
                        icon = MarkerIcons.YELLOW
                        map = naverMap
                    }
                }

                if ((time_idx >= 3) && (car_idx == 4)){
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    println("000000000000000000000000000000000000000000000000000")
                    minute_time += 1
                    time_idx = 0
                    current_state = path_car1.coords.size - minute_time * (path_car1.coords.size / min_movingtime)

                    Marker().apply {
                        position = path_car1.coords[current_state]
                        icon = MarkerIcons.YELLOW
                        map = naverMap
                    }
                }



                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()

        )

        // 버튼 누르면 ! 8 9 순서
        binding.btnSearch.setOnClickListener {
                viewModel.getGeoApi(
                    "uzlzuhd2pa",
                    "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                    binding.etArrive.text.toString().replace(" ", "")
                )

                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

        }
    }

    override fun onDestroy() {
        println("555555555555555555555555555555555555555555555555555555555")
        println("555555555555555555555555555555555555555555555555555555555")
        println("555555555555555555555555555555555555555555555555555555555")
        println("555555555555555555555555555555555555555555555555555555555")
        println("555555555555555555555555555555555555555555555555555555555")

        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

class Car(
    val carNumber: String,
    var battery: Double,
    var currentcarLocation: String,
    var moving: String,
    var targetcarLocation: String,
    var movingtime: Int
){
    fun moveToTarget(): Boolean {
        if (currentcarLocation == targetcarLocation) {
            println("$carNumber is already at its target location")
            println(currentcarLocation)
            println(targetcarLocation)
            return true
        }

        println("$carNumber is moving from $currentcarLocation to $targetcarLocation...")

        if (currentcarLocation != targetcarLocation) {
            if (3 >= (currentcarLocation - targetcarLocation)) {
                currentcarLocation = targetcarLocation
                moving = "0"
            }
            else {
                moving = "1"
            }
            battery -= 0.01667 // 1분에 1프로씩 배터리가 방전되도록 설정
            println("battery : ")
            println(battery)
        }

        if (battery < 10.0) {
            println("$carNumber is low on battery!")
            return false
        }

        println("$carNumber successfully arrived at its target location!")
        return true
    }
}