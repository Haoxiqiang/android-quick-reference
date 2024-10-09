#!/bin/bash

set -euo pipefail

repoDir="${AOSP_PATH}"
target="${AOSP_BACKUP}"
versions=(
    "android-15.0.0_r1" "android-14.0.0_r74" "android-13.0.0_r83" "android-12.1.0_r27" "android-12.0.0_r34"
    "android-11.0.0_r48" "android-10.0.0_r47" "android-9.0.0_r61" "android-8.1.0_r81" "android-8.0.0_r51"
    "android-7.1.2_r39" "android-7.1.1_r61" "android-7.1.0_r7" "android-7.0.0_r36" "android-6.0.1_r81"
    "android-6.0.0_r41" "android-5.1.1_r38" "android-5.1.0_r5" "android-5.0.2_r3" "android-5.0.1_r1"
    "android-5.0.0_r7" "android-4.4w_r1" "android-4.4_r1.2.0.1" "android-4.4.4_r2.0.1" "android-4.4.3_r1.1.0.1"
    "android-4.4.2_r2.0.1" "android-4.4.1_r1.0.1" "android-4.3_r3.1" "android-4.3.1_r1" "android-4.2_r1"
    "android-4.2.2_r1.2" "android-4.2.1_r1.2" "android-4.1.2_r2.1" "android-4.1.1_r6.1" "android-4.0.4_r2.1"
    "android-4.0.3_r1.1" "android-4.0.2_r1" "android-4.0.1_r1.2" "android-3.2.4_r1" "android-2.3_r1"
    "android-2.3.7_r1" "android-2.3.6_r1" "android-2.3.5_r1" "android-2.3.4_r1" "android-2.3.3_r1.1"
    "android-2.3.2_r1" "android-2.3.1_r1" "android-2.2_r1.3" "android-2.2.3_r2.1" "android-2.2.2_r1"
    "android-2.2.1_r2" "android-2.1_r2.1s" "android-2.0_r1" "android-2.0.1_r1" "android-1.6_r2"
)

excludeDirs() {
    local path="$1"
    [[ "$path" == *"tool"* || "$path" == *"tools"* || "$path" == *"cmds"* || "$path" == *"tests"* ]]
}

export -f excludeDirs

log() {
    echo "$(date +'%Y-%m-%d %H:%M:%S') - $1"
}

for version in "${versions[@]}"; do
    log "Copying AOSP $version from $repoDir to $target"
    # Enter the AOSP directory
    if ! cd "$repoDir"; then
        log "Failed to enter $repoDir"
        continue
    fi
    # Checkout the current version
    if ! git checkout -f "$version"; then
        log "Failed to checkout $version"
        continue
    fi

    # Define the target directory for the current version
    targetDir="$target/$version"
    javaDistDir="$targetDir/java"
    cppDistDir="$targetDir/cpp"

    find "$repoDir" -maxdepth 15 -type f \( -name "*.java" -o -name "*.kt" \) ! -path "*/tool/*" ! -path "*/cmds/*" ! -path "*/tests/*" -exec bash -c '
        file="$1"
        repoDir="$2"
        javaDistDir="$3"
        rp="${file#"$repoDir"}"
        tf="$javaDistDir$rp"
        mkdir -p "$(dirname "$tf")" || { echo "Failed to create directory $(dirname "$tf")"; exit 1; }
        cp "$file" "$tf" || { echo "Failed to copy $file to $tf"; exit 1; }
    ' _ {} "$repoDir" "$javaDistDir" \;

    find "$repoDir" -maxdepth 15 -type f \( -name "*.c" -o -name "*.cpp" -o -name "*.h" \) ! -path "*/tool/*" ! -path "*/cmds/*" ! -path "*/tests/*" -exec bash -c '
        file="$1"
        repoDir="$2"
        cppDistDir="$3"
        rp="${file#"$repoDir"}"
        tf="$cppDistDir$rp"
        mkdir -p "$(dirname "$tf")" || { echo "Failed to create directory $(dirname "$tf")"; exit 1; }
        cp "$file" "$tf" || { echo "Failed to copy $file to $tf"; exit 1; }
    ' _ {} "$repoDir" "$cppDistDir" \;
done
