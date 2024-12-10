import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

def read_grid_and_moves(file_path):
    """
    Grid boyutunu ve hamleleri bir .txt dosyasından okur.
    Dosya formatı:
    İlk satır: N = grid boyutu
    Sonraki satırlar: Nokta koordinatları (ör. 1, 2)
    """
    moves = []
    grid_size = 0
    with open(file_path, "r") as file:
        lines = file.readlines()
        # İlk satırdan grid boyutunu oku
        for line in lines:
            line = line.strip()
            if line.startswith("N"):
                grid_size = int(line.split('=')[1].strip())
            else:
                moves.append(tuple(map(int, line.split(", "))))  # Virgülden sonra boşluk var
    return grid_size, moves


def draw_animated_moves(grid_size, moves, interval=500):
    """
    NxN grid üzerinde hamleleri animasyonlu olarak çizer.
    :param grid_size: Grid boyutu (N)
    :param moves: Hamlelerin listesi (merkez noktalar)
    :param interval: İki hamle arasındaki süre (ms cinsinden)
    """
    fig, ax = plt.subplots(figsize=(8, 8))
    
    # Grid'in oluşturulması (satranç tahtası gibi)
    for x in range(grid_size + 1):
        ax.plot([x, x], [0, grid_size], color='black', linewidth=0.5)
        ax.plot([0, grid_size], [x, x], color='black', linewidth=0.5)

    # X ve Y ekseninde 0'dan N-1'e kadar sayılar yazdıralım
    for i in range(grid_size):
        for j in range(grid_size):
            ax.text(j + 0.5, i + 0.5, f"{i},{j}", ha="center", va="center", fontsize=8, color='black')

    # Merkez noktalarının hesaplanması
    centers = {(i, j): (j + 0.5, i + 0.5) for i in range(grid_size) for j in range(grid_size)}

    # Hamleleri animasyon için hazırlık
    dot, = ax.plot([], [], 'ro', markersize=10)  # Kırmızı nokta

    def update(frame):
        """Her kare için güncelleme."""
        if frame < len(moves):
            move = moves[frame]
            if move in centers:
                x, y = centers[move]
                dot.set_data([x], [y])  # x ve y'yi birer liste olarak veriyoruz
                # Çizgiler için
                if frame > 0:
                    prev_move = moves[frame - 1]
                    prev_x, prev_y = centers[prev_move]
                    ax.plot([prev_x, x], [prev_y, y], color="blue", linewidth=2)  # Çizgi ekle
        return dot,

    ani = FuncAnimation(fig, update, frames=len(moves), interval=interval, repeat=False)

    # Tahtayı düzenle
    ax.set_xlim(0, grid_size)
    ax.set_ylim(0, grid_size)
    ax.set_aspect("equal")

    # X ve Y eksenlerinde sadece 0'dan N-1'e kadar olan sayıları gösterelim
    ax.set_xticks(range(grid_size + 1))  # X ekseninde 0'dan N'ye kadar olan sayılar
    ax.set_yticks(range(grid_size + 1))  # Y ekseninde 0'dan N'ye kadar olan sayılar

    # X ve Y eksenlerinin etiketlerinin gösterilmesini sağlamak
    ax.set_xticklabels(range(grid_size + 1))  # X ekseni etiketleri
    ax.set_yticklabels(range(grid_size + 1))  # Y ekseni etiketleri

    plt.show()


# Örnek kullanım
if __name__ == "__main__":
    moves_file = "moves.txt"  # Hamlelerin olduğu dosya

    # Grid boyutunu ve hamleleri dosyadan oku
    grid_size, moves = read_grid_and_moves(moves_file)
    
    # Hamleleri animasyonlu çiz
    draw_animated_moves(grid_size, moves, interval=500)  # 500 ms aralıklarla
