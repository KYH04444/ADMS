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
import android.graphics.Path
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
    var intercept_car_idx = 0

    var time_idx = 0

    var home_to_goal_time = 0

    var path_car1 = PathOverlay()
    var path_car2 = PathOverlay()
    var path_car3 = PathOverlay()
    var path_car4 = PathOverlay()
    var path_car5 = PathOverlay()

    var real_path_car1 = PathOverlay()
    var real_path_car2 = PathOverlay()
    var real_path_car3 = PathOverlay()
    var real_path_car4 = PathOverlay()
    var real_path_car5 = PathOverlay()

    // 초기 세팅 배터리 100프로
    // 경도, 위도
    var cars = arrayOf(
        Car("A", 100.0, "${128.752938356},${35.836285935}", "0", 0,0, 0, 0.0, 0),
        Car("B", 100.0, "${128.752938356},${35.836285935}", "0",0,0,0, 0.0,0),
        Car("C", 100.0, "${128.752938356},${35.836285935}", "0",0,0,0, 0.0,0),
        Car("D", 100.0, "${128.752938356},${35.836285935}", "0",0,0,0, 0.0,0),
        Car("E", 100.0, "${128.752938356},${35.836285935}", "0",0,0,0, 0.0,0)
    )

    override fun initView() {
        setNaverMapRender(R.id.container_direction_map, activity?.supportFragmentManager!!, this)
        // 3번으로 넘어간다.
        // 데이터를 activity에서 받아왔다.
        getDataFromActivity()
        // 2번으로 넘어간다.
        observeData()
    }

    private fun observeData() {
        viewModel.curLocation.observe(this, {
            val location =
                "${it.region?.area1?.name} ${it.region?.area2?.name} ${it.region?.area3?.name} ${it.region?.area4?.name} ${it.land?.number1}-${it.land?.number2}"
            binding.etStart.setText(location)
            // 1 3 2 4찍고 7루프를 계속해서 돌리다가 버튼을 입력하면 8, 9번이 발동된다.


            var m = 0

            // 에러 방지용 코드
            if  (car_idx == 4)
            {

                if ((cars[4].moving == "1") || (cars[4].moving == "2"))
                {
                    m = cars[4].targetcarLocation.size - 1
                    viewModel.getResultPath(
                        "uzlzuhd2pa",
                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                        goalGeocode,
                        cars[4].targetcarLocation[m]
                    )
                }
                else {
                    viewModel.getResultPath(
                        "uzlzuhd2pa",
                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                        goalGeocode,
                        cars[4].currentcarLocation
                    )
                }
            }

            var i = 1

            //LatLng(35.836285935, 128.752938356)
            if (indx == 1) {
                // 목적지와 관제탑사이의 소요시간
                viewModel.getResultPath(
                    "uzlzuhd2pa",
                    "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                    goalGeocode,
                    "128.752938356,35.836285935"
                )

                while(i<7) {
                    if (i >= 5)
                    {
                        i += 1
                    }
                    else {
                        if ((cars[i].moving == "1") || (cars[i].moving == "2"))
                        {
                            m = cars[4].targetcarLocation.size - 1
                            viewModel.getResultPath(
                                "uzlzuhd2pa",
                                "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                                goalGeocode,
                                cars[i].targetcarLocation[m]
                            )
                        }
                        else {
                            viewModel.getResultPath(
                                "uzlzuhd2pa",
                                "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                                goalGeocode,
                                cars[i].currentcarLocation
                            )
                        }
                        i += 1
                    }

                }

                //indx = 3
            }
        })

        // currentLocationLatLng에다가 나의 현위치 또는 내가 설정할 위치 내가 클릭을해서 마커를 형성했다면 내가 설정한 위치를 대입하고
        // 그게 아니라면 디폴트는 나의 현위치로 계산한다.
        // goalGeocode 에 다가는 차의 현위치 또는 차가 도착한 후의 도착 위치
        viewModel.goalLocation.observe(this, {
            println("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")

            goalGeocode = "${viewModel.goalLocation.value?.x},${viewModel.goalLocation.value?.y}"
            var m = 0
//            cars[indx].currentcarLocation
            if ((cars[0].moving == "1") || (cars[0].moving == "2"))
            {
                m = cars[4].targetcarLocation.size - 1
                viewModel.getResultPath(
                    "uzlzuhd2pa",
                    "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                    goalGeocode,
                    cars[0].targetcarLocation[m]
                )
            }

            else {
                viewModel.getResultPath(
                    "uzlzuhd2pa",
                    "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                    goalGeocode,
                    cars[0].currentcarLocation
                )
            }
            println(goalGeocode)
            println(cars[0].currentcarLocation)

            // println("${true.currentLocationLatLng}")
//            println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
//            println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
//            println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
//            println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
//            println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
//            println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
//            println("88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888")
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

            if (intercept_car_idx >= 1)
            {
                if (intercept_car_idx == 0){
                    path_car1.coords = path.coords
                }

                if (intercept_car_idx == 1){
                    path_car2.coords = path.coords
                }

                if (intercept_car_idx == 2){
                    path_car3.coords = path.coords
                }

                if (intercept_car_idx == 3){
                    path_car4.coords = path.coords
                }

                if (intercept_car_idx == 4){
                    path_car5.coords = path.coords
                }
                intercept_car_idx = 0
            }
            else
            {
            if (car_idx == 0){
                path_car1.coords = path.coords
            }

            else if (car_idx == 1){
                path_car2.coords = path.coords
            }

            else if (car_idx == 2){
                path_car3.coords = path.coords
            }

            else if (car_idx == 3){
                path_car4.coords = path.coords
            }

            else if (car_idx == 4){
                path_car5.coords = path.coords
            }
            }

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

            if (indx == 1)
            {
                home_to_goal_time = it[0].summary.duration / 60000
                indx = 2
            }

            else if (indx == 2){
                cars[car_idx].movingtime = it[0].summary.duration / 60000
                car_idx += 1
                println(car_idx)

                if (car_idx == 5)
                {
                    indx = 3
                }
            }

            else if (indx == 0){
                cars[car_idx].movingtime = it[0].summary.duration / 60000
                car_idx += 1
                println(car_idx)
            }

            // 최종적으로 맵을 형성
            binding.btnNavigation.visibility = View.VISIBLE

//            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
//            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
//            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
//            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
//            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
//            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")
//            println("99999999999999999999999999999999999999999999999999999999999999999999999999999999")

            if (indx == 0){
                indx = 1
            }

        }
    }

    // 데이터를 activity에서 받아왔다.
    private fun getDataFromActivity() {
//        println("33333333333333333333333333333333333333333333333333333333333333333333333")
//        println("33333333333333333333333333333333333333333333333333333333333333333333333")
//        println("33333333333333333333333333333333333333333333333333333333333333333333333")
//        println("33333333333333333333333333333333333333333333333333333333333333333333333")
//        println("33333333333333333333333333333333333333333333333333333333333333333333333")

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
//        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
//        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
//        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
//        println("444444444444444444444444444444444444444444444444444444444444444444444444444")
//        println("444444444444444444444444444444444444444444444444444444444444444444444444444")

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

        // 1초에 한 번
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

                var p = 0


                // 업무중일 때 체크를 하며 업무끝나면 다시 경로 생성

                while (p <= 4) {
                    if (cars[p].moving == "2") {
                        cars[p].working_time += 0.016666

                        if (cars[p].working_time >= 10) {
                            cars[p].minute_time = 0
                            cars[p].realmovingtime.removeAt(0)
                            cars[p].targetcarLocation.removeAt(0)
                            cars[p].working_time = 0.0

                            // 이제 api를 다시 보낸다.
                            if (cars[p].targetcarLocation.size != 0) {
                                if (p == 0) {
                                    intercept_car_idx = 1
                                    viewModel.getResultPath(
                                        "uzlzuhd2pa",
                                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                                        cars[0].targetcarLocation[0],
                                        cars[0].currentcarLocation
                                    )
                                } else if (p == 1) {
                                    intercept_car_idx = 2
                                    viewModel.getResultPath(
                                        "uzlzuhd2pa",
                                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                                        cars[1].targetcarLocation[0],
                                        cars[1].currentcarLocation
                                    )
                                } else if (p == 2) {
                                    intercept_car_idx = 3
                                    viewModel.getResultPath(
                                        "uzlzuhd2pa",
                                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                                        cars[2].targetcarLocation[0],
                                        cars[2].currentcarLocation
                                    )
                                } else if (p == 3) {
                                    intercept_car_idx = 4
                                    viewModel.getResultPath(
                                        "uzlzuhd2pa",
                                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                                        cars[3].targetcarLocation[0],
                                        cars[3].currentcarLocation
                                    )
                                } else if (p == 4) {
                                    intercept_car_idx = 5
                                    viewModel.getResultPath(
                                        "uzlzuhd2pa",
                                        "INnDxBgwB6Tt20sjSdFEqi6smxIBUNp4r7EkDUBc",
                                        cars[4].targetcarLocation[0],
                                        cars[4].currentcarLocation
                                    )
                                }
                            }
                        }
                    }
                    p += 1
                }


                // 갑분 발표 자료 만들기
                // 그리고 실제로 운용할 때는 소요시간으로 계산하지 않고 그냥 gps받아와서 계산한다고 설명한다.


                // api키 보낼때 이동중인 차량은 잘 고려해서 보내기


                //***********************************************************************************************************************************************
                //***********************************************************************************************************************************************
                // 매칭된 차량 사후 관리 수정
                // 마커에 그려주기
                //***********************************************************************************************************************************************
                //***********************************************************************************************************************************************

                time_idx += 1
                var m = 0
                var current_state = 0
                var moving_car_count = 0

                while (m <= 4) {
                    println("들어오긴 ..?")
                    println(cars[m].moving)




                    if (m == 0) {
                        println("들어오긴 ..? 제발")
                        // 갈 때만 경로를 찍어준다.
                        if ((cars[m].moving == "1") || (cars[m].moving == "5")) {
                            moving_car_count += 1
                            if (time_idx >= 3) {
                                time_idx = 0
                                cars[m].minute_time += 1

                                current_state =
                                    path_car1.coords.size - cars[m].minute_time * (path_car1.coords.size / cars[m].realmovingtime[0])

                                if (current_state < 0) {
                                    println("$m 번차가 목적지에 도착했습니다.")
                                    // 도착했으면 변수들 초기화 시켜주기
                                    cars[m].minute_time =
                                        0                                                // 다음 목적지까지 가고 있는 시간
                                    cars[m].currentcarLocation =
                                        cars[m].targetcarLocation[0]              // 가장 최근 목적지로 현위치 이동
                                } else {
                                    Marker().apply {
                                        position = path_car1.coords[current_state]
                                        icon = MarkerIcons.YELLOW
                                        map = naverMap
                                    }
                                    // 나중에 이거 순서맞는지 체크
                                    cars[m].currentcarLocation =
                                        "${path_car1.coords[current_state].longitude},${path_car1.coords[current_state].latitude}"
                                }
                            }
                        }
                    }

                    else if (m == 1) {

                        // 갈 때만 경로를 찍어준다.
                        if ((cars[m].moving == "1") || (cars[m].moving == "5")) {

                            moving_car_count += 1
                            if (time_idx >= 3) {
                                println("여기다 여기")
                                println(path_car2.coords.size)
                                time_idx = 0
                                cars[m].minute_time += 1

                                current_state =
                                    path_car2.coords.size - cars[m].minute_time * (path_car2.coords.size / cars[m].realmovingtime[0])

                                if (current_state < 0) {
                                    println("$m 번차가 목적지에 도착했습니다.")
                                    // 도착했으면 변수들 초기화 시켜주기
                                    cars[m].minute_time =
                                        0                                                // 다음 목적지까지 가고 있는 시간
                                    cars[m].currentcarLocation =
                                        cars[m].targetcarLocation[0]              // 가장 최근 목적지로 현위치 이동
                                } else {
                                    Marker().apply {
                                        position = path_car2.coords[current_state]
                                        icon = MarkerIcons.YELLOW
                                        map = naverMap
                                    }
                                    // 나중에 이거 순서맞는지 체크
                                    cars[m].currentcarLocation =
                                        "${path_car2.coords[current_state].longitude},${path_car2.coords[current_state].latitude}"
                                }
                            }
                        }
                    }


                else if (m == 2) {

                    // 갈 때만 경로를 찍어준다.
                    if ((cars[m].moving == "1") || (cars[m].moving == "5")) {

                        moving_car_count += 1
                        if (time_idx >= 3) {
                            time_idx = 0
                            cars[m].minute_time += 1

                            current_state =
                                path_car3.coords.size - cars[m].minute_time * (path_car3.coords.size / cars[m].realmovingtime[0])

                            if (current_state < 0) {
                                println("$m 번차가 목적지에 도착했습니다.")
                                // 도착했으면 변수들 초기화 시켜주기
                                cars[m].minute_time =
                                    0                                                // 다음 목적지까지 가고 있는 시간
                                cars[m].currentcarLocation =
                                    cars[m].targetcarLocation[0]              // 가장 최근 목적지로 현위치 이동
                            } else {
                                Marker().apply {
                                    position = path_car3.coords[current_state]
                                    icon = MarkerIcons.YELLOW
                                    map = naverMap
                                }
                                // 나중에 이거 순서맞는지 체크
                                cars[m].currentcarLocation =
                                    "${path_car3.coords[current_state].longitude},${path_car3.coords[current_state].latitude}"
                            }
                        }
                    }
                }



                        else if (m == 3) {

                            // 갈 때만 경로를 찍어준다.
                            if ((cars[m].moving == "1") ||(cars[m].moving == "5")) {

                                moving_car_count += 1
                                if (time_idx >= 3) {
                                    time_idx = 0
                                    cars[m].minute_time += 1

                                    current_state =
                                        path_car4.coords.size - cars[m].minute_time * (path_car4.coords.size / cars[m].realmovingtime[0])

                                    if (current_state < 0) {
                                        println("$m 번차가 목적지에 도착했습니다.")
                                        // 도착했으면 변수들 초기화 시켜주기
                                        cars[m].minute_time =
                                            0                                                // 다음 목적지까지 가고 있는 시간
                                        cars[m].currentcarLocation =
                                            cars[m].targetcarLocation[0]              // 가장 최근 목적지로 현위치 이동
                                    } else {
                                        Marker().apply {
                                            position = path_car4.coords[current_state]
                                            icon = MarkerIcons.YELLOW
                                            map = naverMap
                                        }
                                        // 나중에 이거 순서맞는지 체크
                                        cars[m].currentcarLocation =
                                            "${path_car4.coords[current_state].longitude},${path_car4.coords[current_state].latitude}"
                                    }
                                }
                            }
                        }

                        else if (m == 4) {

                            // 갈 때만 경로를 찍어준다.
                            if ((cars[m].moving == "1") ||(cars[m].moving == "5")) {
                                moving_car_count += 1
                                if (time_idx >= 3) {
                                    time_idx = 0
                                    cars[m].minute_time += 1

                                    current_state =
                                        path_car5.coords.size - cars[m].minute_time * (path_car5.coords.size / cars[m].realmovingtime[0])

                                    if (current_state < 0) {
                                        println("$m 번차가 목적지에 도착했습니다.")
                                        // 도착했으면 변수들 초기화 시켜주기
                                        cars[m].minute_time =
                                            0                                                // 다음 목적지까지 가고 있는 시간
                                        cars[m].currentcarLocation =
                                            cars[m].targetcarLocation[0]              // 가장 최근 목적지로 현위치 이동
                                    } else {
                                        Marker().apply {
                                            position = path_car5.coords[current_state]
                                            icon = MarkerIcons.YELLOW
                                            map = naverMap
                                        }
                                        // 나중에 이거 순서맞는지 체크
                                        cars[m].currentcarLocation =
                                            "${path_car5.coords[current_state].longitude},${path_car5.coords[current_state].latitude}"
                                    }
                                }
                            }
                        }
                    m += 1
                    }




                // movingtime 초기화 시켜주어야...
                // ㅇㅇ이건 매칭이 마무리 되면 movingtime초기화 시켜주면 된다. ㅇㅇ  그리고 매칭이 마무리되면 targetcarsLocation도 목적지로 변경해주어야한다!

                //***********************************************************************************************************************************************
                //***********************************************************************************************************************************************
                // 차량 매칭 파트
                //***********************************************************************************************************************************************
                //***********************************************************************************************************************************************

                var k = 0
                var min_movingtime = 100000
                var matching_car_idx = -1

                // real_movingtime 만들기

                // 평가하기전에 일단 배터리잔량을 고려했을때 다시 관제탑으로 돌아올 수 있는지 여부를 판정
                // 아무도 갈 수 없다고 판단시에 "현재 가용한 차량이 없습니다."라는 문구 띄우기
                // 또한 아무도 갈 수 없다고 판단시에

                var total_score = -1.0
                var real_total_score = 0.0

                // 출발했을때의 배터리 잔량 변수를 생성해야한다. battery 필요없을 듯 현상태의 배터리만 알면될 듯
                // real_movingtime에 들어있는 값들을 모두 더해야한다.
                // 거기에 내가 이동하고 있는 시간에 real_movingtime의 첫번째 원소와 차이를 구한 후
                // 남아있는 다른 real_movingtime을 모두 더한 후 그 값과 movingtime을 더하고 추가로 관제탑과 목적지까지의 소요시간을 미리 받아두고 그 소요시간을 더하고 업무시간들 까지 더한 후 현재 내 배터리보다 작으면 일단 출동은 할 수 있다고 본다.
                // 그리고 real_movingtime에다가 업무시간을 더한다. 업무시간은 1건당 10분으로 책정한다.
                // real_movingtime을 리스트로 받을까 싶기도? 한쪽에 도달하면 가장 앞에 있는 리스트를 삭제해주는 방향으로


                // 이동중이냐 아니냐 먼저 판단 --> 가능한 배터리 --> 소요시간
                // 모두 추적이 끝났다면 어떤 차와 매칭할지 결정
                // 매칭이되면 real_movingtime마지막 인덱스에 추가

                var f = 0
                var sum = 0.0
                if (car_idx == 5) {
                    while (k < 5) {



                        // 가는중
                        if (cars[k].moving == "1") {
                             //소요시간 sum
                             //소요시간 구할 때 일하고 있는 놈이 있을 수 있다. 고려해서 변수생성해서 계산하자
                            sum =
                                sumList(cars[k].realmovingtime) - cars[k].realmovingtime[0] + (cars[k].realmovingtime[0] - cars[k].minute_time) + cars[k].realmovingtime.size * 10 + cars[k].movingtime + 0.0

                            // 일거리들을 다 해치우고도 컴백할 수 있을 때
                            if (cars[k].battery >= sum + 10 + home_to_goal_time) {
                                // total_score 계산하자
                                // 평가할 항목들 : 다시 관제탑으로 돌아왔을때의 배터리 잔량(1~5), moving여부(0,2), 소요시간이 가장 우선순위 10, 9 ,8 ,7, 6 ....
                                // 다시 관제탑으로 돌아왔을때의 배터리 잔량(1~5)
                                total_score += (cars[k].battery - (sum + 10 + home_to_goal_time)) / 20.0

                                // 소요시간
                                if (sum < 10) {
                                    total_score += 10
                                } else if (sum < 20) {
                                    total_score += 9
                                } else if (sum < 30) {
                                    total_score += 8
                                } else if (sum < 40) {
                                    total_score += 7
                                } else if (sum < 50) {
                                    total_score += 6
                                } else {
                                    total_score += 5
                                }
                                // moving여부 가는 중
                                total_score += 0

                                // 하루 업무량
                                if (cars[k].today_doing_task_count <= 30) {
                                    total_score += cars[k].today_doing_task_count / 6
                                } else {
                                    total_score += 5
                                }
                            }
                        }

                        // 업무중
                        else if (cars[k].moving == "2") {
                            //소요시간 sum
                            //소요시간 구할 때 일하고 있는 놈이 있을 수 있다. 고려해서 변수생성해서 계산하자
                            sum =
                                sumList(cars[k].realmovingtime) - cars[k].realmovingtime[0] + (cars[k].realmovingtime.size-1) * 10 + cars[k].movingtime + cars[k].working_time

                            // 일거리들을 다 해치우고도 컴백할 수 있을 때
                            if (cars[k].battery >= sum + 10 + home_to_goal_time) {
                                // total_score 계산하자
                                // 평가할 항목들 : 다시 관제탑으로 돌아왔을때의 배터리 잔량(1~5), moving여부(0,2), 소요시간이 가장 우선순위 10, 9 ,8 ,7, 6 ....
                                // 다시 관제탑으로 돌아왔을때의 배터리 잔량(1~5)
                                total_score += (cars[k].battery - (sum + 10 + home_to_goal_time)) / 20.0

                                // 소요시간
                                if (sum < 10) {
                                    total_score += 10
                                } else if (sum < 20) {
                                    total_score += 9
                                } else if (sum < 30) {
                                    total_score += 8
                                } else if (sum < 40) {
                                    total_score += 7
                                } else if (sum < 50) {
                                    total_score += 6
                                } else {
                                    total_score += 5
                                }
                                // moving여부 가는 중
                                total_score += 0.5

                                // 하루 업무량
                                if (cars[k].today_doing_task_count <= 30) {
                                    total_score += cars[k].today_doing_task_count / 6
                                } else {
                                    total_score += 5
                                }
                            }
                        }




                        // 목적지가 없을 때  moving 0,3  집, 복귀중
                        else {
                            sum = cars[k].movingtime + 0.0
                            // 컴백할 수 있을 때
                            if (cars[k].battery >= sum + 10 + home_to_goal_time) {

                                //println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^")
                                // total_score 계산하자
                                // 평가할 항목들 : 다시 관제탑으로 돌아왔을때의 배터리 잔량(1~5), moving여부(0,2), 소요시간이 가장 우선순위 10, 9 ,8 ,7, 6 ....
                                // 다시 관제탑으로 돌아왔을때의 배터리 잔량(1~5)
                                total_score += (cars[k].battery + 30 - (cars[k].movingtime + 10 + home_to_goal_time)) / 20.0

                                // 소요시간
                                if (sum < 10) {
                                    total_score += 10
                                } else if (sum < 20) {
                                    total_score += 9
                                } else if (sum < 30) {
                                    total_score += 8
                                } else if (sum < 40) {
                                    total_score += 7
                                } else if (sum < 50) {
                                    total_score += 6
                                } else {
                                    total_score += 5
                                }
                                // moving여부(0,2)
                                if (cars[k].moving == "0") {
                                    total_score += 2
                                }
                                else {
                                    total_score += 1
                                }
                                //println(total_score)

                                // 하루 업무량
                                if (cars[k].today_doing_task_count <= 30) {
                                    total_score += cars[k].today_doing_task_count / 6
                                } else {
                                    total_score += 5
                                }
                            }
                        }

                        // total 점수로 비교
                        if (real_total_score <= total_score) {
                            real_total_score = total_score
                            matching_car_idx = k
                            println(matching_car_idx)
                        }
                            k += 1
                            total_score = -1.0


                    }
                    // while 끝

                    // 매칭된거임
                    if (matching_car_idx != -1) {
                        cars[matching_car_idx].moving = "5"
                        cars[matching_car_idx].targetcarLocation.add(goalGeocode)
                        cars[matching_car_idx].realmovingtime.add(cars[matching_car_idx].movingtime)
                        cars[matching_car_idx].today_doing_task_count += 1
                        home_to_goal_time = 0
                        println(cars[matching_car_idx].moving)
                        println(cars[matching_car_idx].targetcarLocation)
                        println(cars[matching_car_idx].realmovingtime)
                        println(cars[matching_car_idx].today_doing_task_count)
                        // 아... 내가 이걸 위에서 초기화해버렸었네
                        println(cars[matching_car_idx].movingtime)
                        println("234rtfrdewqefgrewerghbgre345yuhgfewq2w3e4rtyhgfdewqwerty")
                    }

                    else{
                        println("현재 매칭이 가능하지 않습니다.")
                        if (home_to_goal_time >= 45)
                        {
                            println("배송이 불가한 위치입니다.")
                        }
                    }
//                    // 매칭완료되면 변수들 초기화해주기
//                    // movingtime 초기화해주기
                    car_idx = 0
                    home_to_goal_time = 0
                    while (f<=4){
                        cars[f].movingtime = 0
                        f+=1
                    }
                }

                println("@@@222222222222222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@22222222222222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2222222222222222222@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@2222222222222222@@@@@@@@@@@@")
                println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@222222222222222222@@@@@@@@@@@@")
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
//            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//            println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        }
    }

    override fun onDestroy() {
//        println("555555555555555555555555555555555555555555555555555555555")
//        println("555555555555555555555555555555555555555555555555555555555")
//        println("555555555555555555555555555555555555555555555555555555555")
//        println("555555555555555555555555555555555555555555555555555555555")
//        println("555555555555555555555555555555555555555555555555555555555")

        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun sumList(list: List<Int>): Int {
        var sum = 0
        for (num in list) {
            sum += num
        }
        return sum
    }
}

class Car(
    val carNumber: String,
    var battery: Double,
    var currentcarLocation: String,
    var moving: String,
    var movingtime: Int,
    var minute_time: Int,
    var today_doing_task_count: Int,
    var working_time: Double,
    var matching_stack: Int,
    var targetcarLocation: MutableList<String> = mutableListOf(),
    var realmovingtime: MutableList<Int> = mutableListOf()
){
    // 배터리 관리 + 상태 체크
    fun moveToTarget(): Boolean {
        if (matching_stack >= 10) {
            matching_stack = -1
        }

        if ((moving == "5") && matching_stack >=0)
        {
            matching_stack += 1
                battery -= 0.01667 // 1분에 1프로씩 배터리가 방전되도록 설정
                println("매칭된 차의 battery : ")
                println(battery)

                if (battery <= 0) {
                    battery = 0.0
                    println("$carNumber 배터리 0프로.. 정지")
                }
        }


        else
        {
            matching_stack = 0
            if (currentcarLocation == "${128.752938356},${35.836285935}") {
                // 집
                moving = "0"
                battery += 0.01667
                if (battery >= 100) {
                    battery = 100.0
                }
            } else {
                battery -= 0.01667 // 1분에 1프로씩 배터리가 방전되도록 설정
                println("매칭된 차의 battery : ")
                println(battery)

                if (battery <= 0) {
                    battery = 0.0
                    println("$carNumber 배터리 0프로.. 정지")
                }

                if (targetcarLocation.size >= 1) {
                    // 업무중
                    if ((currentcarLocation == targetcarLocation[0]) && moving == "3") {
                        moving = "2"
                    }
                    // 가는중
                    else {
                        moving = "1"
                    }
                }

                // 복귀중
                else {
                    moving = "3"
                }
            }
        }

        if (battery < 10.0) {
            println("$carNumber is low on battery!")
        }
        return true
    }
}