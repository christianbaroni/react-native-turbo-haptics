cmake_minimum_required(VERSION 3.22.1)
project("turbo-haptics")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(NODE_MODULES_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../../node_modules")

add_library(turbo-haptics SHARED
        TurboHapticsHostObject.cpp
        "${NODE_MODULES_DIR}/react-native/ReactCommon/jsi/jsi/jsi.cpp")

target_include_directories(turbo-haptics PRIVATE
        "${CMAKE_CURRENT_SOURCE_DIR}/includes"
        "${NODE_MODULES_DIR}/react-native/ReactCommon"
        "${NODE_MODULES_DIR}/react-native/ReactCommon/jsi"
        "${NODE_MODULES_DIR}/react-native/ReactCommon/jsi/jsi"
)

find_library(log-lib log)

target_link_libraries(turbo-haptics
        android
        ${log-lib})