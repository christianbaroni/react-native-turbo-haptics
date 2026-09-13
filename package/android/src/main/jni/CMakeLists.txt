cmake_minimum_required(VERSION 3.22.1)
project("turbo-haptics")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(turbo-haptics SHARED
        TurboHapticsBindings.cpp)

find_package(ReactAndroid REQUIRED CONFIG)
find_package(fbjni REQUIRED CONFIG)

target_link_libraries(turbo-haptics
        fbjni::fbjni
        ReactAndroid::jsi)

if(TURBO_HAPTICS_NEW_ARCHITECTURE)
    target_compile_definitions(turbo-haptics PRIVATE
            TURBO_HAPTICS_NEW_ARCHITECTURE=1)

    target_link_libraries(turbo-haptics
            ReactAndroid::reactnative)
else()
    find_library(log-lib log)
    target_link_libraries(turbo-haptics ${log-lib})
endif()
